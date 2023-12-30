/**
 * Keepaste - The keep and paste program (http://www.keepaste.com)
 * Copyright (C) 2023 Tamir Krispis
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.keepaste.logic.managers;

import com.keepaste.logic.Application;
import com.keepaste.logic.exceptions.KeepExecutionException;
import com.keepaste.logic.exceptions.KeepParameterExecutionException;
import com.keepaste.logic.models.Keep;
import com.keepaste.logic.models.KeepParameter;
import com.keepaste.logic.models.WindowInformation;
import com.keepaste.logic.utils.ClipboardUtils;
import com.keepaste.logic.utils.GuiUtils;
import com.keepaste.logic.utils.KeyboardUtils;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is a {@code SwingWorker} that handles Keep execution.
 */
@Log4j2
public class KeepExecutionWorker extends SwingWorker<Void, String> {
    private static final String FAILED_TO_EXECUTE_KEEP = "Failed to execute keep";
    private final Keep keep;
    private final boolean refreshParameters;
    private final WindowInformation targetWindow;
    private final TreePath treeSelectedPath;

    /**
     * a {@code SwingWorker} to execute a keep on the currently active window.
     *
     * @param keep              the {@code Keep} to execute
     * @param refreshParameters if to refresh the global parameters
     */
    public KeepExecutionWorker(Keep keep, boolean refreshParameters) {
        this.keep = keep;
        this.refreshParameters = refreshParameters;

        // clearing selection from tree not to accidentally executing the selected node when the user presses the 'enter'
        // key (like on dialogs)
        treeSelectedPath = getAndClearTreeSelection();

        // keeping the active window that the user meant to paste on
        targetWindow = Application.getContext().getModelActiveWindow().getActiveWindow();
    }

    @Override
    protected Void doInBackground() {
        // handling parameters and getting final command to execute
        String commandToExecute = processParameters(keep, refreshParameters);

        log.info("Final Keep command to execute [{}]", commandToExecute);

        if (!StringUtils.isEmpty(commandToExecute)) {
            // copying to clipboard always takes place
            copyToClipboard(commandToExecute);

            // running execution flow based on flow settings
            runExecutionFlow();
        }
        return null;
    }

    @Override
    protected void done() {
        log.debug("Completing keep execution");
        // hiding the temp background label
        Application.getContext().getGui().labelBackground.setVisible(false);
        // setting false flag saying there is no currently running keep
        Application.getContext().setKeepCurrentlyRunning(false);
        // starting back the window interceptor
        Application.getContext().startWindowInterceptorRunner();
        // setting back the tree selection path (was removed to prevent from executing keep nodes when pressing 'enter' while keep is running, like when interacting with parameter dialogs)
        Application.getContext().getGui().tree.setSelectionPath(treeSelectedPath);
        log.debug("Keep execution completed");
    }

    /**
     * Will run the keep execution flow.
     */
    private void runExecutionFlow() {
        // if next flow phase is not enabled, initiate only copy
        if (!Application.getContext().getModelSettings().isFocusOnWindowAndPaste()) {
            GuiUtils.showTargetWindowLabelMessage("Keep copied, ready to paste...", 1);
        } else {
            // if next flow phase is enabled, then focusing on active window

            if (isFocusOnActiveWindow(targetWindow)) {
                pasteKeep();
            } else {
                handleWrongTargetWindow();
            }

            // if next floe phase is enabled, then pressing enter
            if (Application.getContext().getModelSettings().isPressEnterAfterPaste()) {
                pressEnter(keep);
            }
        }
    }

    /**
     * Will process a keep's parameters and return the final keep command to be executed with parameters values.
     *
     * @param keep              the executed {@code Keep}
     * @param refreshParameters if to refresh and request the global parameters again
     * @return the final keep command to be executed with parameters values
     */
    private String processParameters(Keep keep, boolean refreshParameters) {
        log.debug("Processing keep parameters for keep [{}]", keep);
        String commandToExecute = keep.getPhrase();

        // a map to hold parameters and their resolved values
        Map<String, String> currentParameterValuesMap = new HashMap<>();

        if (isKeepHasParameters(keep)) {
            for (KeepParameter parameter : keep.getParameters()) {
                log.debug("Processing parameter [{}]", parameter);
                try {
                    executeParameter(parameter, keep, refreshParameters, currentParameterValuesMap);
                } catch (KeepParameterExecutionException ex) {
                    log.error(FAILED_TO_EXECUTE_KEEP, ex);
                    return null; // cancelling
                }
            }

            // setting parameter values to command
            commandToExecute = setParametersValuesToCommand(currentParameterValuesMap, commandToExecute);
        }

        return commandToExecute;
    }

    /**
     * Setting resolved parameter values to the command to be executed.
     *
     * @param currentParameterValuesMap the map with resolved parameter values
     * @param commandToExecute          the command to be executed
     * @return the final command to execute
     */
    private static String setParametersValuesToCommand(Map<String, String> currentParameterValuesMap, String commandToExecute) {
        for (Map.Entry<String, String> entry : currentParameterValuesMap.entrySet()) {
            commandToExecute = commandToExecute.replace(String.format("<%s>", entry.getKey()), entry.getValue());
        }
        return commandToExecute;
    }

    /**
     * Checks if the keep has parameters.
     *
     * @param keep  the {@code Keep}
     * @return true if the keep has parameters, otherwise false
     */
    private static boolean isKeepHasParameters(Keep keep) {
        return keep.getParameters() != null && !keep.getParameters().isEmpty();
    }

    /**
     * Copies the command to execute to clipboard.
     *
     * @param commandToExecute the command to be executed and copied to clipboard
     */
    private static void copyToClipboard(String commandToExecute) {
        // setting final keep command to the clipboard
        log.debug("Setting [{}] to clipboard", commandToExecute);
        ClipboardUtils.setValue(commandToExecute);
    }

    /**
     * Clears and return the current tree selection.
     * This is used in order to prevent from executing keeps while another keep is running (by pressing 'enter' while interacting with dialogs during execution).
     *
     * @return the current tree selection that was cleared
     */
    private static TreePath getAndClearTreeSelection() {
        var currentSelectionPath = Application.getContext().getGui().tree.getSelectionPath();
        Application.getContext().getGui().tree.clearSelection();
        return currentSelectionPath;
    }

    /**
     * Validates that keepaste is focused on the active window where to execute the keep.
     *
     * @param currentlyActiveWindow the currently active window.
     * @return true if focused, false otherwise
     */
    private static boolean isFocusOnActiveWindow(WindowInformation currentlyActiveWindow) {
        if (currentlyActiveWindow == null) {
            JOptionPane.showMessageDialog(Application.getContext().getGui(),
                    "Please select a window by clicking on it in order to run Keeps",
                    "No active window", JOptionPane.WARNING_MESSAGE);
            throw new KeepExecutionException("No active window is set");
        } else {
            return Application.getContext().getWindowManager().focusOnActiveWindow(currentlyActiveWindow);
        }
    }

    /**
     * Imitate pressing the 'enter' key.
     *
     * @param keep the executed {@code Keep}
     */
    private static void pressEnter(Keep keep) {
        if (keep.isNeverPressEnter()) {
            log.debug("keep is set to never press 'enter' so it didn't");
        } else {
            log.debug("Imitating \"ENTER\" key");
            KeyboardUtils.enter();
        }
    }

    /**
     * Pastes whatever was copied to the clipboard.
     */
    private static void pasteKeep() {
        log.debug("pasting ".concat(ClipboardUtils.getValue().toString()));
        Application.getContext().getWindowManager().paste();
    }

    /**
     * Handles a state where a different target window is the current active window than the one when keep execution started.
     */
    private static void handleWrongTargetWindow() {
        JOptionPane.showMessageDialog(Application.getContext().getGui(),
                "Seems like keepaste is not focused on the desired window, please try again...",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        log.debug("Not focused on correct window so not pasting and pressing ENTER");
    }

    /**
     * Will manage a Keep parameter.
     *
     * @param parameter                 the {@code KeepParameter} to handle
     * @param keep                      the {@code Keep} in context
     * @param isRefreshGlobalParameters         if to refresh the global parameters
     * @param currentParameterValuesMap the current global parameters cache
     */
    private void executeParameter(
            @NonNull final KeepParameter parameter,
            @NonNull final Keep keep,
            final boolean isRefreshGlobalParameters,
            @NonNull final Map<String, String> currentParameterValuesMap) {

        // in case of this is a global parameter, checking if the value already exist for it then setting it on the current param values map
        takeParamValueFromGlobalMapIfExists(parameter, currentParameterValuesMap, isRefreshGlobalParameters);

        // in case the value doesn't exist yet
        if (!isParamValueAlreadyResolved(parameter, currentParameterValuesMap)) {

            // Array-type parameter, getting values to choose from
            List<String> paramValues = handleArrayTypeParamPhrase(parameter);

            // Command-type parameter, getting values to choose from
            if (paramValues == null) {
                paramValues = handleCommandTypeParamPhrase(parameter, keep, currentParameterValuesMap, isRefreshGlobalParameters);
            }

            // a parameter to hold the resolved parameter value
            String selectedParamValue;

            // if there are values to choose from
            if (paramValues != null) {
                // showing a dialog for the user to choose a resolved value from
                selectedParamValue = displayParamValuesOptionsDialog(parameter, paramValues);
                log.debug("Value of [{}] was chosen from an options dialog [{}]", selectedParamValue, parameter);
            } else {
                // free text parameter, ask the user for an input
                log.debug("Parameter [{}] is of a free-text type", parameter);

                selectedParamValue = JOptionPane.showInputDialog(
                        Application.getContext().getGui().getContentPane(),
                        String.format("Input a value for %s", parameter.getName()),
                        "Set parameter value",
                        JOptionPane.QUESTION_MESSAGE);
                log.debug("Value of [{}] was set to free-text parameter [{}]", selectedParamValue, parameter);
            }

            // in case no value was given from this parameter
            if (StringUtils.isEmpty(selectedParamValue)) {
                JOptionPane.showMessageDialog(
                        Application.getContext().getGui(),
                        String.format("Value for parameter \"%s\" is not set, cancelling processing the Keep",
                                parameter.getName())
                );
                log.debug(
                        "The user probably clicked on the cancel button on the dialog to set a free-text for "
                                + "the parameter [{}]", parameter);
                throw new KeepParameterExecutionException("User cancelled");
            } else {
                // if this parameter was given a resolved value
                if (keep.getPhrase() != null && !StringUtils.isEmpty(selectedParamValue)) {
                    // setting the value to the current resolved values map
                    addParamValueToCurrentValuesMap(parameter, currentParameterValuesMap, selectedParamValue);
                    // if it is a global parameter, adding it to the global parameters map as well
                    addParamValueToGlobalValues(parameter, selectedParamValue);
                }
            }
        }
    }


    /**
     * Will set the already resolved parameter value if exists on the global parameters map.
     *
     * @param parameter                 the {@code KeepParameter}
     * @param currentParameterValuesMap the current parameter values map
     * @param refreshParameters         if to refresh the global parameters
     */
    private void takeParamValueFromGlobalMapIfExists(KeepParameter parameter,
                                                     Map<String, String> currentParameterValuesMap,
                                                     boolean refreshParameters) {
        // checking if we already have a value for this parameter on the global parameters (unless refresh parameters was chosen)
        if (isParamGlobalValueSet(parameter, refreshParameters)) {
            takeCurrentParamValueFromGlobal(parameter, currentParameterValuesMap);
        }
    }

    /**
     * Will handle a parameter of an array type ['a','b','c']
     * @param parameter the {@code KeepParameter}
     * @return a list of possible values for the user to choose from
     */
    private List<String> handleArrayTypeParamPhrase(KeepParameter parameter) {
        List<String> keepResult = null;
        if (isNotFreeTextTypeParam(parameter) && parameter.getPhrase().startsWith("[")) {
            // predefined array of values
            keepResult = Arrays.stream(parameter.getPhrase().substring(1, parameter.getPhrase().length() - 1)
                            .split(",")).map(String::trim)
                    .collect(Collectors.toList());
            log.debug("Parameter [{}] is of array type, values=[{}]", parameter, keepResult);
        }
        return keepResult;
    }


    /**
     * Will return true if a param has a resolved value on the global resolved values map.
     *
     * @param parameter         the {@code KeepParameter}
     * @param refreshParameters if to refresh the global parameters
     * @return true if a param has a resolved value on the global resolved values map.
     */
    private static boolean isParamGlobalValueSet(KeepParameter parameter, boolean refreshParameters) {
        return !refreshParameters && parameter.isGlobal() && KeepExecutionManager.GLOBAL_PARAMETER_VALUES_MAP.containsKey(parameter.getName());
    }

    /**
     * Take a resolved value, if exists in global resolved values map.
     *
     * @param parameter                 the {@code KeepParameter}
     * @param currentParameterValuesMap the current resolved parameters map
     */
    private static void takeCurrentParamValueFromGlobal(KeepParameter parameter, Map<String, String> currentParameterValuesMap) {
        String selectedParamValue;
        selectedParamValue = KeepExecutionManager.GLOBAL_PARAMETER_VALUES_MAP.get(parameter.getName());
        currentParameterValuesMap.put(parameter.getName(), selectedParamValue);
        log.debug("Parameter [{}] was taken from global parameters, selected value=[{}]", parameter, selectedParamValue);
    }

    /**
     * Will return true if this is a command/array type of parameter (not free-text).
     *
     * @param parameter the {@code KeepParameter}
     * @return true if this is a command/array type of parameter (not free-text).
     */
    private static boolean isNotFreeTextTypeParam(KeepParameter parameter) {
        return parameter.getPhrase() != null && !parameter.getPhrase().isEmpty();
    }

    /**
     * Will return true if a param already has a resolved value on the current resolved values map.
     *
     * @param parameter                 the {@code KeepParameter}
     * @param currentParameterValuesMap the current resolved values map
     * @return true if a param already has a resolved value on the current resolved values map.
     */
    private static boolean isParamValueAlreadyResolved(KeepParameter parameter, Map<String, String> currentParameterValuesMap) {
        return currentParameterValuesMap.containsKey(parameter.getName());
    }

    /**
     * Adds a resolved value to the current resolved values map.
     *
     * @param parameter                 the {@code KeepParameter}
     * @param currentParameterValuesMap the current resolved values map
     * @param value        the resolved value
     */
    private static void addParamValueToCurrentValuesMap(
            KeepParameter parameter,
            Map<String, String> currentParameterValuesMap,
            String value) {
        log.debug("Adding value of [{} for parameter [{}] to the current parameters values map", value, parameter);
        currentParameterValuesMap.put(parameter.getName(), value);
    }

    /**
     * Adds a resolved value to the global resolved values map.
     *
     * @param parameter                 the {@code KeepParameter}
     * @param value        the resolved value
     */
    private static void addParamValueToGlobalValues(KeepParameter parameter, String value) {
        if (parameter.isGlobal()) {
            log.debug(
                    "As it is set to be global, adding value of [{}] for parameter [{}] to the global "
                            + "parameters values map", value, parameter);
            KeepExecutionManager.GLOBAL_PARAMETER_VALUES_MAP.put(parameter.getName(), value);
        }
    }

    /**
     * Displays an options dialog for the user to choose a resolved value from.
     *
     * @param parameter     the {@code KeepParameter}
     * @param valuesToChooseFrom    the list of values to choose from
     * @return the selected value
     */
    private String displayParamValuesOptionsDialog(KeepParameter parameter, List<String> valuesToChooseFrom) {
        String selectedParamValue;
        log.debug("Showing the user a dialog to choose a value for tha parameter [{}]", parameter);
        selectedParamValue = (String) JOptionPane.showInputDialog(
                Application.getContext().getGui().getContentPane(),
                String.format("Choose a value for %s", parameter.getName()),
                "Set parameter value",
                JOptionPane.QUESTION_MESSAGE,
                null,
                parseToLineByLine(valuesToChooseFrom),
                null);
        log.debug("User selected the value [{}] for parameter [{}]", selectedParamValue, parameter);
        if (StringUtils.isEmpty(selectedParamValue)) {
            JOptionPane.showMessageDialog(
                    Application.getContext().getGui(),
                    String.format(
                            "Value for parameter \"%s\" is not set, cancelling processing the Keep",
                            parameter.getName())
            );
            log.debug(
                    "The user probably clicked on the cancel button on the dialog to choose a value "
                            + "for the parameter [{}]", parameter);
            throw new KeepParameterExecutionException("User cancelled");
        }
        return selectedParamValue;
    }

    /**
     * Will execute the parameter command and return a list of values for the user to choose from.
     *
     * @param parameter                 the {@code KeepParameter}
     * @param currentParameterValuesMap the currently resolved values map
     * @return the selected value       if to refresh the global resolved values map
     */
    private List<String> handleCommandTypeParamPhrase(
            KeepParameter parameter, Keep keep, Map<String, String> currentParameterValuesMap, boolean isRefreshGlobalParameters) {
        List<String> keepResult = null;

        if (isNotFreeTextTypeParam(parameter)) {
            try {
                String paramKeepString = parameter.getPhrase();

                log.debug("Parameter [{}] is of Command type, executing param command [{}]", parameter, paramKeepString);

                // first setting all already resolved parameters values to the command of the param as it may use params as well
                paramKeepString = setParametersValuesToCommand(currentParameterValuesMap, paramKeepString);

                // checking if the param Keep uses parameters as well, and if so, executing those first
                for (KeepParameter innerParameter : keep.getParameters()) {
                    if (paramKeepString.contains(String.format("<%s>", innerParameter.getName()))) {
                        // this parameter uses another one, so executing it first
                        executeParameter(innerParameter, keep, isRefreshGlobalParameters, currentParameterValuesMap);
                        paramKeepString = paramKeepString.replace(
                                String.format("<%s>", innerParameter.getName()),
                                currentParameterValuesMap.get(innerParameter.getName()));
                    }
                }

                keepResult = Application.getContext().getKeepExecutionManager().executeCommand(paramKeepString);
                log.debug("Parameter [{}], Keep result=[{}]", parameter, keepResult);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        Application.getContext().getGui().getContentPane(),
                        String.format(
                                "Failed to run Keep \"%s\" for parameter \"%s\". %s.",
                                parameter.getPhrase(), parameter.getName(), e.getMessage()),
                        "Error when running a Keep",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
        return keepResult;
    }


    /**
     * Will break a result into a clean list of values to choose from seperated by line breaks.
     *
     * @param stringToParse the list of strings returned as a result from a command
     * @return a clean list of values to choose from seperated by line breaks
     */
    private String[] parseToLineByLine(List<String> stringToParse) {
        if (stringToParse.size() == 1 && stringToParse.get(0).startsWith("[") && stringToParse.get(0).endsWith("]")) {
            // this is a one-liner json array, splitting it, the next 'if' statement will process it
            String jsonArr = stringToParse.get(0);
            jsonArr = jsonArr.substring(1, jsonArr.length() - 1);
            stringToParse = Arrays.asList(jsonArr.split(","));
        }

        if (!stringToParse.isEmpty() && stringToParse.get(0).equals("[")) {
            // in case of a json array, an option to return visible and actual values for the dropdown lists (name of ec2
            // instance to display, the id to use on the command)
            // parse from a json array
            stringToParse.remove(0); // the opening bracket
            stringToParse.remove(stringToParse.size() - 1); // the closing bracket
            stringToParse.replaceAll(s -> s.replace("\",", "").replace("\"", "").trim()); // removing ", and "
        }

        Collections.sort(stringToParse);

        return stringToParse.toArray(new String[0]);
    }

}

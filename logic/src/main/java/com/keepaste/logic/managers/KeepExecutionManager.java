package com.keepaste.logic.managers;

import com.keepaste.logic.Application;
import com.keepaste.logic.exceptions.KeepExecutionException;
import com.keepaste.logic.exceptions.KeepParameterExecutionException;
import com.keepaste.logic.models.Keep;
import com.keepaste.logic.models.KeepParameter;
import com.keepaste.logic.models.WindowInformation;
import com.keepaste.logic.utils.ClipboardUtils;
import com.keepaste.logic.utils.FileSystemUtils;
import com.keepaste.logic.utils.KeyboardUtils;
import com.keepaste.logic.utils.OperatingSystemUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import javax.swing.*;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static com.keepaste.logic.common.Constants.ONLY_COPY_MODE;

/**
 * This class hods methods related to Keep execution.
 */
@Log4j2
public final class KeepExecutionManager {
    public static final String FAILED_TO_EXECUTE_KEEP = "Failed to execute keep";
    @Getter
    private static final Map<String, String> GLOBAL_PARAMETER_VALUES_MAP = new HashMap<>();
    public static final int COMMAND_EXEC_TIMEOUT = 15;

    private String shell = "/bin/bash";

    /**
     * Constructor.
     *
     * @throws IOException          on error
     * @throws InterruptedException on error
     */
    public KeepExecutionManager() {
        if (OperatingSystemUtils.getOperatingSystemType() != OperatingSystemUtils.OperatingSystemType.WINDOWS) {
            try {
                List<String> shellValue = executeCommandWithDefaultPath("echo $SHELL");
                if (!shellValue.isEmpty()) {
                    shell = shellValue.get(0);
                    log.debug("Shell set to [{}]", shell);
                }
            } catch (Exception ex) {
                log.error(String.format("Failed to get shell, using default shell of [%s]", shell), ex);
                if (ex instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    /**
     * Executed a selected {@link Keep} on the currently active window.
     *
     * @param keep the {@link Keep} to execute
     */
    public void executeKeepOnWindow(Keep keep) {
        executeKeepOnWindow(keep, false);
    }

    /**
     * Executed a selected {@link Keep} on the currently active window.
     *
     * @param keep the {@link Keep} to execute
     * @param refreshParameters true if we wish to clear the existing global parameters and have a fresh start
     */
    public void executeKeepOnWindow(Keep keep, boolean refreshParameters) {
        log.info(
                "Executing Keep [{}] on window [{}] with refresh parameters [{}]",
                keep.toStringAll(), Application.getContext().getModelActiveWindow(), refreshParameters);

        // clearing selection from tree not to accidentally executing the selected node when the user presses the 'enter'
        // key (like on dialogs)
        var currentSelectionPath = Application.getContext().getGui().tree.getSelectionPath();
        Application.getContext().getGui().tree.clearSelection();

        // stopping window interception while running the Keep to prevent pasting on wrong window
        Application.getContext().stopWindowInterceptorRunner();

        // keeping the active window that the user meant to paste on
        WindowInformation currentlyActiveWindow = Application.getContext().getModelActiveWindow().getActiveWindow();

        // validating that only one keep is running at a time
        if (Application.getContext().isKeepCurrentlyRunning()) {
            JOptionPane.showMessageDialog(Application.getContext().getGui(),
                    "Can only run one Keep at a time, please wait for the other Keep to finish or abort it",
                    "One Keep at a time please", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Application.getContext().setKeepCurrentlyRunning(true);

        // preventing from enter being pressed on dialogs shown while manipulating parameters from triggering the tree's
        // key listener (prevents from running another keep by mistake when pressing ENTER)
        Optional<KeyListener> viewTreeKeyListener =
                Arrays.stream(Application.getContext().getGui().tree.getKeyListeners())
                        .filter(keyListener -> keyListener.getClass().getName().contains("ViewTree"))
                        .findFirst();
        viewTreeKeyListener.ifPresent(keyListener -> Application.getContext().getGui().tree.removeKeyListener(keyListener));

        Application.getContext().getGui().labelBackground.setText("Executing keep...");
        Application.getContext().getGui().labelBackground.setVisible(true);
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String keepToExecute = manipulateParameters(keep, refreshParameters);
                log.info("Final Keep to execute [{}]", keepToExecute);
                if (!StringUtils.isEmpty(keepToExecute)) {

                    copyToClipboard(keepToExecute);

                    if (!Application.getContext().getModelSettings().isFocusOnWindowAndPaste()) {
                        handleOnlyCopy();
                    } else {
                        if (isFocusOnActiveWindow(currentlyActiveWindow)) {
                            pasteKeep();
                            pressEnter(keep);
                        } else {
                            handleWrongTargetWindow();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                Application.getContext().getGui().labelBackground.setVisible(false);
                Application.getContext().setKeepCurrentlyRunning(false);

                viewTreeKeyListener.ifPresent(keyListener -> Application.getContext().getGui().tree.addKeyListener(keyListener));

                Application.getContext().setKeepCurrentlyRunning(false);
                Application.getContext().startWindowInterceptorRunner();

                Application.getContext().getGui().tree.setSelectionPath(currentSelectionPath);

                log.debug("Keep execution finished");
            }
        };
        worker.execute();
    }

    private static void copyToClipboard(String keepToExecute) {
        // setting final keep command to the clipboard
        log.debug("Setting [{}] to clipboard", keepToExecute);
        ClipboardUtils.setValue(keepToExecute);
    }

    private static void handleWrongTargetWindow() {
        JOptionPane.showMessageDialog(Application.getContext().getGui(),
                "Seems like keepaste is not focused on the desired window, please try again...",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        log.debug("Not focused on correct window so not pasting and pressing ENTER");
    }

    private static void pressEnter(Keep keep) {
        if (Application.getContext().getModelSettings().isPressEnterAfterPaste()) {
            if (keep.isNeverPressEnter()) {
                log.debug("keep is set to never press 'enter' so it didn't");
            } else {
                log.debug("Imitating \"ENTER\" key");
                KeyboardUtils.enter();
            }
        }
    }

    private static void pasteKeep() {
        if (Application.getContext().getModelSettings().isFocusOnWindowAndPaste()) {
            log.debug("pasting ".concat(ClipboardUtils.getValue().toString()));
            Application.getContext().getWindowManager().paste();
        }
    }

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
     * Will execute a command in shell and return its output as a list of Strings.
     *
     * @param command the command to execute
     * @return  the execution output
     * @throws IOException in case of execution failure
     * @throws InterruptedException in case of execution failure
     */
    public List<String> executeCommandWithDefaultPath(String command) throws KeepExecutionException, IOException, InterruptedException {
        return executeCommand(List.of(command), true);
    }

    /**
     * Will execute a command in shell and return its output as a list of Strings.
     *
     * @param command the command to execute
     * @return  the execution output
     * @throws IOException in case of execution failure
     * @throws InterruptedException in case of execution failure
     */
    public List<String> executeCommand(String command) throws KeepExecutionException, IOException, InterruptedException {
        return executeCommand(List.of(command), false);
    }

    /**
     * Will execute a command in shell and return its output as a list of Strings.
     *
     * @param commandLines  the command lines to execute
     * @param defaultPath   will use the default PATH env var and not the one set by the user, used for Keepaste internal
     *                      commands (like intercepting the currently active window)
     * @return  the execution output
     * @throws IOException in case of execution failure
     * @throws InterruptedException in case of execution failure
     */
    public List<String> executeCommand(List<String> commandLines, boolean defaultPath)
            throws KeepExecutionException, IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        // Set the working directory for the process
        processBuilder.directory(new File(FileSystemUtils.getUserHomeDirectory()));

        List<String> newKeep = new ArrayList<>();

        OperatingSystemUtils.OperatingSystemType os = OperatingSystemUtils.getOperatingSystemType();
        switch (os.name()) {
            case OperatingSystemUtils.WINDOWS:
                newKeep.add("powershell.exe");
                newKeep.add("-Command");
                newKeep.addAll(commandLines);
                processBuilder.command(newKeep);
                break;
            case OperatingSystemUtils.MAC:
            case OperatingSystemUtils.LINUX:
            case OperatingSystemUtils.OTHER:
            default:
                if (!defaultPath) {
                    // Get the environment variables
                    Map<String, String> environment = processBuilder.environment();
                    environment.put("PATH", Application.getContext().getModelSettings().getPath());
                }

                newKeep.add(shell);
                newKeep.add("-c");
                newKeep.addAll(commandLines);
                processBuilder.command(newKeep);
                break;
        }
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        InputStream inputStream = process.getInputStream();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream));

        int timeout = COMMAND_EXEC_TIMEOUT;
        do {
            timeout--;
            process.waitFor(1, TimeUnit.SECONDS);
        } while (process.isAlive() && timeout > 0);

        if (timeout <= 0) {
            throw new KeepExecutionException("Command timeout exceeded (10 sec.)");
        }

        String line;
        List<String> outputLines = new ArrayList<>();
        if (inputStream.available() > 0) {
            while ((line = reader.readLine()) != null) {
                outputLines.add(line);
            }
        }
        inputStream.close();
        return outputLines;
    }

    /**
     * Will manage a Keep parameter.
     *
     * @param parameter                 the {@link KeepParameter} to handle
     * @param keep                      the {@link Keep} in context
     * @param isRefreshGlobalParameters         if to refresh the global parameters
     * @param currentParameterValuesMap the current global parameters cache
     * @throws Exception in case of any error
     */
    private void executeParameter(
            @NonNull final KeepParameter parameter,
            @NonNull final Keep keep,
            final boolean isRefreshGlobalParameters,
            @NonNull final Map<String, String> currentParameterValuesMap) {

        // in case of this is a global parameter, checking if the value already exist for it
        validateValueSetForGlobalParam(parameter, currentParameterValuesMap, isRefreshGlobalParameters);

        // in case the value doesn't exist yet
        if (!ifParamValueSet(parameter, currentParameterValuesMap)) {

            // Array-type parameter
            List<String> paramValues = handleArrayTypeParamPhrase(parameter);

            // Command-type parameter
            if (paramValues == null) {
                paramValues = handleCommandTypeParamPhrase(parameter, keep, currentParameterValuesMap, isRefreshGlobalParameters);
            }

            String selectedParamValue = null;
            if (paramValues != null) {
                selectedParamValue = displayParamValuesOptionsDialog(parameter, paramValues);
            } else {
                // free text parameter
                log.debug("Parameter [{}] is of a free-text type", parameter);

                selectedParamValue = JOptionPane.showInputDialog(
                        Application.getContext().getGui().getContentPane(),
                        String.format("Input a value for %s", parameter.getName()),
                        "Set parameter value",
                        JOptionPane.QUESTION_MESSAGE);
                log.debug("Value of [{}] was set to free-text parameter [{}]", selectedParamValue, parameter);
            }

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
                if (keep.getPhrase() != null && !StringUtils.isEmpty(selectedParamValue)) {
                    addParamValueToCurrentValues(parameter, currentParameterValuesMap, selectedParamValue);
                    addParamValueToGlobalValues(parameter, selectedParamValue);
                }
            }
        }
    }

    private static void addParamValueToCurrentValues(
            KeepParameter parameter,
            Map<String, String> currentParameterValuesMap,
            String selectedParamValue) {
        log.debug("Adding value of [{} for parameter [{}] to the current parameters values map", selectedParamValue, parameter);
        currentParameterValuesMap.put(parameter.getName(), selectedParamValue);
    }

    private static void addParamValueToGlobalValues(KeepParameter parameter, String selectedParamValue) {
        if (parameter.isGlobal()) {
            log.debug(
                    "As it is set to be global, adding value of [{}] for parameter [{}] to the global "
                            + "parameters values map", selectedParamValue, parameter);
            GLOBAL_PARAMETER_VALUES_MAP.put(parameter.getName(), selectedParamValue);
        }
    }

    private String displayParamValuesOptionsDialog(KeepParameter parameter, List<String> keepResult) {
        String selectedParamValue;
        log.debug("Showing the user a dialog to choose a value for tha parameter [{}]", parameter);
        selectedParamValue = (String) JOptionPane.showInputDialog(
                Application.getContext().getGui().getContentPane(),
                String.format("Choose a value for %s", parameter.getName()),
                "Set parameter value",
                JOptionPane.QUESTION_MESSAGE,
                null,
                parseToLineByLine(keepResult),
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

    private List<String> handleCommandTypeParamPhrase(
            KeepParameter parameter, Keep keep, Map<String, String> currentParameterValuesMap, boolean isRefreshGlobalParameters) {
        List<String> keepResult = null;

        if (!isFreeTextTypeParam(parameter)) {
            try {
                String paramKeepString = parameter.getPhrase();

                log.debug("Parameter [{}] is of Command type, executing param command [{}]", parameter, paramKeepString);
                paramKeepString = populateParamPhraseWithAlreadySetParams(parameter, currentParameterValuesMap, paramKeepString);

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

                keepResult = executeCommand(paramKeepString);
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


    private static String populateParamPhraseWithAlreadySetParams(
            KeepParameter parameter, Map<String, String> currentParameterValuesMap, String paramKeepString) {
        // filling existing parameters values if already chosen and used in the next parameter
        for (Map.Entry<String, String> currParam : currentParameterValuesMap.entrySet()) {
            paramKeepString = paramKeepString.replace(String.format("<%s>", currParam.getKey()), currParam.getValue());
            log.debug("Parameter [{}] was taken from current run parameters, selected value=[{}]", parameter, currParam.getValue());
        }
        return paramKeepString;
    }

    private static boolean isFreeTextTypeParam(KeepParameter parameter) {
        return parameter.getPhrase() == null || parameter.getPhrase().isEmpty();
    }

    private static boolean ifParamValueSet(KeepParameter parameter, Map<String, String> currentParameterValuesMap) {
        return currentParameterValuesMap.containsKey(parameter.getName());
    }

    private static void setParamValueFromGlobal(KeepParameter parameter, Map<String, String> currentParameterValuesMap) {
        String selectedParamValue;
        selectedParamValue = GLOBAL_PARAMETER_VALUES_MAP.get(parameter.getName());
        currentParameterValuesMap.put(parameter.getName(), selectedParamValue);
        log.debug("Parameter [{}] was taken from global parameters, selected value=[{}]", parameter, selectedParamValue);
    }

    private static boolean isParamGlobalValueSet(KeepParameter parameter, boolean refreshParameters) {
        return !refreshParameters && parameter.isGlobal() && GLOBAL_PARAMETER_VALUES_MAP.containsKey(parameter.getName());
    }


    /* ***************** PRIVATE METHODS ***************** */

    private String manipulateParameters(Keep keep, boolean refreshParameters) {
        log.debug("Manipulating parameters");
        // manipulating parameters

        String keepToExecute = keep.getPhrase();
        Map<String, String> currentParameterValuesMap = new HashMap<>();
        if (keep.getParameters() != null && !keep.getParameters().isEmpty()) {
            for (KeepParameter parameter : keep.getParameters()) {
                log.debug("Manipulating parameter [{}]", parameter);

                try {
                    executeParameter(parameter, keep, refreshParameters, currentParameterValuesMap);
                } catch (KeepParameterExecutionException ex) {
                    log.error(FAILED_TO_EXECUTE_KEEP, ex);
                    return null; // cancelling
                }
            }

            for (Map.Entry<String, String> entry : currentParameterValuesMap.entrySet()) {
                keepToExecute = keepToExecute.replace(String.format("<%s>", entry.getKey()), entry.getValue());
            }
        }

        return keepToExecute;
    }

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
            stringToParse.remove(0); // the opening '['
            stringToParse.remove(stringToParse.size() - 1); // the closing ']'
            stringToParse.replaceAll(s -> s.replace("\",", "").replace("\"", "").trim()); // removing ", and "
        }

        Collections.sort(stringToParse);

        return stringToParse.toArray(new String[stringToParse.size()]);
    }

    private void handleOnlyCopy() {
        new Thread(() -> {
            Application.getContext().getGui().labelTargetWindow.setText("Keep copied, ready to paste...");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                log.error("Sleep interrupted exception", e);
                Thread.currentThread().interrupt();
            }
            Application.getContext().getGui().labelTargetWindow.setText(ONLY_COPY_MODE);
        }).start();
    }

    private void validateValueSetForGlobalParam(KeepParameter parameter,
                                                Map<String, String> currentParameterValuesMap,
                                                boolean refreshParameters) {
        // checking if we already have a value for this parameter on the global parameters (unless refresh parameters was chosen)
        if (isParamGlobalValueSet(parameter, refreshParameters)) {
            setParamValueFromGlobal(parameter, currentParameterValuesMap);
        }
    }

    private List<String> handleArrayTypeParamPhrase(KeepParameter parameter) {
        List<String> keepResult = null;
        if (!isFreeTextTypeParam(parameter) && parameter.getPhrase().startsWith("[")) {
            // predefined array of values
            keepResult = Arrays.stream(parameter.getPhrase().substring(1, parameter.getPhrase().length() - 1)
                            .split(",")).map(String::trim)
                    .collect(Collectors.toList());
            log.debug("Parameter [{}] is of array type, values=[{}]", parameter, keepResult);
        }
        return keepResult;
    }
}

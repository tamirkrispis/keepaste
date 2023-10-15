package com.keepaste.logic.managers;

import com.keepaste.logic.Application;
import com.keepaste.logic.models.Keep;
import com.keepaste.logic.models.KeepParameter;
import com.keepaste.logic.models.WindowInformation;
import com.keepaste.logic.utils.ClipboardUtils;
import com.keepaste.logic.utils.KeyboardUtils;
import com.keepaste.logic.utils.OperatingSystemUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import javax.swing.*;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
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
    private static final Map<String, String> globalParameterValuesMap = new HashMap<>();

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
            protected Void doInBackground() throws InterruptedException {
                try {
                    String keepToExecute = manipulateParameters(keep, refreshParameters);
                    log.info("Final Keep to execute [{}]", keepToExecute);
                    if (!StringUtils.isEmpty(keepToExecute)) {
                        log.debug("Setting [{}] to clipboard", keepToExecute);
                        ClipboardUtils.setValue(keepToExecute);
                        if (!Application.getContext().getModelSettings().isFocusOnWindowAndPaste()) {
                            new Thread(() -> {
                                Application.getContext().getGui().labelTargetWindow.setText("Keep copied, ready to paste...");
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    log.error("Sleep interrupted exception", e);
                                    Thread.currentThread().interrupt();
                                }
                                Application.getContext().getGui().labelTargetWindow.setText(ONLY_COPY_MODE);
                            }).start();
                        } else {
                            if (Application.getContext().getWindowManager().focusOnActiveWindow(currentlyActiveWindow)) {
                                if (Application.getContext().getModelSettings().isFocusOnWindowAndPaste()) {
                                    log.debug("pasting ".concat(ClipboardUtils.getValue().toString()));
                                    Application.getContext().getWindowManager().paste();
                                }

                                if (Application.getContext().getModelSettings().isPressEnterAfterPaste()) {
                                    if (keep.isNeverPressEnter()) {
                                        log.debug("keep is set to never press 'enter' so it didn't");
                                    } else {
                                        log.debug("Imitating \"ENTER\" key");
                                        KeyboardUtils.enter();
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(Application.getContext().getGui(),
                                        "Seems like keepaste is not focused on the desired window, please try again...",
                                        "Warning",
                                        JOptionPane.WARNING_MESSAGE);
                                log.debug("Not focused on correct window so not pasting and pressing ENTER");
                            }
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(Application.getContext().getGui(),
                            FAILED_TO_EXECUTE_KEEP,
                            "Bummer...",
                            JOptionPane.ERROR_MESSAGE);
                    log.error(FAILED_TO_EXECUTE_KEEP, e);
                    throw e;
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

                log.debug("Keep execution finished");
            }
        };
        worker.execute();

    }

    /**
     * Will execute a command in shell and return its output as a list of Strings.
     *
     * @param command the command to execute
     * @return  the execution output
     * @throws IOException in case of execution failure
     * @throws InterruptedException in case of execution failure
     */
    public List<String> executeCommand(String command) throws Exception {
        return executeCommand(List.of(command));
    }

    /**
     * Will execute a command in shell and return its output as a list of Strings.
     *
     * @param commandLines the command lines to execute
     * @return  the execution output
     * @throws IOException in case of execution failure
     * @throws InterruptedException in case of execution failure
     */
    public List<String> executeCommand(List<String> commandLines) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> newKeep = new ArrayList<>();

        OperatingSystemUtils.OperatingSystemType os = OperatingSystemUtils.getOperatingSystemType();
        switch (os.name()) {
            case OperatingSystemUtils.WINDOWS:
//                newKeep.add("cmd.exe");
//                newKeep.add("/c");
//                newKeep.addAll(commandLines);
//                processBuilder.command(newKeep);

                newKeep.add("powershell.exe");
                newKeep.add("-Command");
                newKeep.addAll(commandLines);
                processBuilder.command(newKeep);
                break;
            case OperatingSystemUtils.MAC:
            case OperatingSystemUtils.LINUX:
            case OperatingSystemUtils.OTHER:
            default:
                newKeep.add("sh");
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

        int timeout = 10;
        do {
            timeout--;
            process.waitFor(1, TimeUnit.SECONDS);
        } while (process.isAlive() && timeout > 0);

        if (timeout <= 0) {
            throw new RuntimeException("Command timeout exceeded (10 sec.)");
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
     * @param refreshParameters         if to refresh the global parameters
     * @param currentParameterValuesMap the current global parameters cache
     * @throws IOException
     * @throws InterruptedException
     */
    private void executeParameter(
            KeepParameter parameter,
            Keep keep,
            boolean refreshParameters,
            Map<String, String> currentParameterValuesMap) throws Exception {
        String selectedParamValue = null;
        // checking if we already have a value for this parameter (unless refresh parameters was chosen)
        if (!refreshParameters && parameter.isGlobal() && globalParameterValuesMap.containsKey(parameter.getName())) {
            selectedParamValue = globalParameterValuesMap.get(parameter.getName());
            currentParameterValuesMap.put(parameter.getName(), selectedParamValue);
            log.debug("Parameter [{}] was taken from global parameters, selected value=[{}]", parameter, selectedParamValue);
        } else if (!currentParameterValuesMap.containsKey(parameter.getName())) {
            if (parameter.getPhrase() != null && !parameter.getPhrase().isEmpty()) {
                List<String> keepResult = null;

                // Keep parameter
                String paramKeepString = parameter.getPhrase();

                // filling existing parameters values if already chosen and used in the next parameter
                for (Map.Entry<String, String> currParam : currentParameterValuesMap.entrySet()) {
                    if (currentParameterValuesMap.containsKey(currParam.getKey())) {
                        paramKeepString = paramKeepString.replace(String.format("<%s>", currParam.getKey()), currParam.getValue());
                        log.debug("Parameter [{}] was taken from current run parameters, selected value=[{}]", parameter, currParam.getValue());
                    }
                }

                if (parameter.getPhrase().startsWith("[")) {
                    // predefined array of values
                    keepResult = Arrays.stream(parameter.getPhrase().substring(1, parameter.getPhrase().length() - 1)
                                    .split(",")).map(String::trim)
                            .collect(Collectors.toList());
                    log.debug("Parameter [{}] is of array type, values=[{}]", parameter, keepResult);
                } else {
                    if (!StringUtils.isEmpty(paramKeepString)) {
                        try {
                            log.debug("Parameter [{}] is of Command type, executing param command [{}]", parameter, paramKeepString);

                            // checking if the param Keep uses parameters as well, and if so, executing those first
                            for (KeepParameter innerParameter : keep.getParameters()) {
                                if (paramKeepString.contains(String.format("<%s>", innerParameter.getName()))) {
                                    // this parameter uses another one, so executing it first
                                    executeParameter(innerParameter, keep, refreshParameters, currentParameterValuesMap);
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
                            throw e;
                        }
                    }
                }
                if (keepResult != null) {
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
                        throw new RuntimeException("User cancelled");
                    }
                }
            } else {
                log.debug("Parameter [{}] is of a free-text type", parameter);
                // free text parameter
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
                throw new RuntimeException("User cancelled");
            } else {
                if (keep.getPhrase() != null && !StringUtils.isEmpty(selectedParamValue)) {
                    log.debug("Adding value of [{} for parameter [{}] to the current parameters values map", selectedParamValue, parameter);
                    currentParameterValuesMap.put(parameter.getName(), selectedParamValue);
                    if (parameter.isGlobal()) {
                        log.debug(
                                "As it is set to be global, adding value of [{}] for parameter [{}] to the global "
                                        + "parameters values map", selectedParamValue, parameter);
                        globalParameterValuesMap.put(parameter.getName(), selectedParamValue);
                    }
                }
            }
        }
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
                } catch (InterruptedException ex) {
                    log.error(FAILED_TO_EXECUTE_KEEP, ex);
                    Thread.currentThread().interrupt();
                }
                catch (Exception ex) {
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
            // in case of a json array, an option to return visible and actual values for the dropdown lists (name of ec2 instance to display, the id to use on the command)
            // parse from a json array
            stringToParse.remove(0); // the opening '['
            stringToParse.remove(stringToParse.size() - 1); // the closing ']'
            stringToParse.replaceAll(s -> s.replace("\",", "").replace("\"", "").trim()); // removing ", and "
        }

        Collections.sort(stringToParse);

        return stringToParse.toArray(new String[stringToParse.size()]);
    }
}

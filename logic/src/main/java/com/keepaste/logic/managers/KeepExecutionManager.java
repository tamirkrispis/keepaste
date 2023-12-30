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
import com.keepaste.logic.models.Keep;
import com.keepaste.logic.utils.FileSystemUtils;
import com.keepaste.logic.utils.OperatingSystemUtils;
import lombok.extern.log4j.Log4j2;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This class hods methods related to Keep execution.
 */
@Log4j2
public final class KeepExecutionManager {
    public static final Map<String, String> GLOBAL_PARAMETER_VALUES_MAP = new HashMap<>();
    public static final int COMMAND_EXEC_TIMEOUT = 15;

    private String shell = "/bin/bash";

    /**
     * Constructor.
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
     * Executed a selected {@code Keep} on the currently active window.
     *
     * @param keep the {@code Keep} to execute
     */
    public void executeKeepOnWindow(Keep keep) {
        executeKeepOnWindow(keep, false);
    }

    /**
     * Executed a selected {@code Keep} on the currently active window.
     *
     * @param keep the {@code Keep} to execute
     * @param refreshParameters true if we wish to clear the existing global parameters and have a fresh start
     */
    public void executeKeepOnWindow(Keep keep, boolean refreshParameters) {
        log.info(
                "Executing Keep [{}] on window [{}] with refresh parameters [{}]",
                keep.toStringAll(), Application.getContext().getModelActiveWindow(), refreshParameters);

        // stopping window interception while running the Keep to prevent pasting on wrong window
        Application.getContext().stopWindowInterceptorRunner();

        // validating that only one keep is running at a time
        if (isAKeepAlreadyRunning()) return;

        // setting keep is running
        Application.getContext().setKeepCurrentlyRunning(true);

        showExecutingKeepLabel();

        // executing the command on another thread not to block the main GUI thread
        new KeepExecutionWorker(keep, refreshParameters).execute();
    }

    private static void showExecutingKeepLabel() {
        Application.getContext().getGui().labelBackground.setText("Executing keep...");
        Application.getContext().getGui().labelBackground.setVisible(true);
    }

    private static boolean isAKeepAlreadyRunning() {
        if (Application.getContext().isKeepCurrentlyRunning()) {
            JOptionPane.showMessageDialog(Application.getContext().getGui(),
                    "Can only run one Keep at a time, please wait for the other Keep to finish or abort it",
                    "One Keep at a time please", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
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
}

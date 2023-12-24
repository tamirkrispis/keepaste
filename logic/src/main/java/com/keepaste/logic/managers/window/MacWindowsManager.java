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

package com.keepaste.logic.managers.window;

import com.keepaste.logic.Application;
import com.keepaste.logic.managers.KeepsManager;
import com.keepaste.logic.models.WindowInformation;
import com.keepaste.logic.utils.FileSystemUtils;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class is Mac's {@code WindowManager}, it holds relevant methods related to windows management in Mac's OS's.
 */
@Log4j2
public final class MacWindowsManager implements WindowManager {
    public static final String GET_TOP_MOST_WINDOW_APPLESCRIPT_FILENAME = "GetTopMostWindow.applescript";
    public static final int GET_ACTIVE_WINDOW_NUM_TRIES = 3;
    public static final int SLEEP_INTERVAL_BETWEEN_RETRIES = 150;
    public static final int SLEEP_AFTER_PASTE_IN_MS = 50;
    private String lastTopMostWindowResult;

    /**
     * Constructor.
     */
    public MacWindowsManager() {
        delAppleScriptFilesForRefresh();
    }

    @Override
    public WindowInformation getActiveWindow() {
        String topMostWindowResult = null;
        try {
            int tries = GET_ACTIVE_WINDOW_NUM_TRIES;
            do {
                topMostWindowResult = runAppleScriptFile(GET_TOP_MOST_WINDOW_APPLESCRIPT_FILENAME);

                if (topMostWindowResult != null && !topMostWindowResult.equals(lastTopMostWindowResult)) {
                    if (!topMostWindowResult.startsWith("success")) {
                        log.error(topMostWindowResult);
                    } else {
                        log.debug("top most window = [{}]", topMostWindowResult);
                        lastTopMostWindowResult = topMostWindowResult;
                        topMostWindowResult = topMostWindowResult.replace("}", "");
                        topMostWindowResult = topMostWindowResult.replace("{", "");
                        topMostWindowResult = topMostWindowResult.replace(" ", "");
                        String[] activeWindowSegments = topMostWindowResult.split(",");
                        String text = activeWindowSegments[2];
                        String app = activeWindowSegments[1];
                        return WindowInformation.builder()
                                .text(text)
                                .app(app)
                                .top(0)
                                .bottom(0)
                                .left(0)
                                .right(0)
                                .processId(Integer.parseInt(activeWindowSegments[GET_ACTIVE_WINDOW_NUM_TRIES]))
                                .build();

                    }
                    lastTopMostWindowResult = topMostWindowResult;
                }
                TimeUnit.MILLISECONDS.sleep(SLEEP_INTERVAL_BETWEEN_RETRIES);
                tries--;
            } while (tries > 0);
        } catch (InterruptedException ex) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            log.debug(String.format("Failed to get active window, top most window=[%s]",
                    topMostWindowResult == null ? "null" : topMostWindowResult), ex);
        }
        return null;
    }

    @Override
    public void paste() {
        log.debug("Pasting using osascript for CMD+V (Apple)");
        try {
            Application.getContext().getKeepExecutionManager().executeCommand(
                    "osascript -e 'tell application \"System Events\" to key code {9} using command down'");
            TimeUnit.MILLISECONDS.sleep(SLEEP_AFTER_PASTE_IN_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Failed to paste on Mac", e);
        }
    }

    @Override
    public boolean focusOnActiveWindow(@NonNull final WindowInformation windowContext) {
        try {
            log.debug("Switching to next window");
            Application.getContext().getKeepExecutionManager().executeCommandWithDefaultPath(
                    "osascript -e 'tell application \"System Events\" to key code 118 using control down'");
            // validating that the window is the desired one
            return Application.getContext().getModelActiveWindow().getActiveWindow().equals(windowContext);
        } catch (InterruptedException e) {
            log.error(String.format("Failed to focus on window [%s]", windowContext), e);
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            log.error(String.format("Failed to focus on window [%s]", windowContext), e);
            return false;
        }
    }


    /**
     * Runs applescript files.
     *
     * @param filename      the filename of the applescript file
     * @param params       relevant command parameters
     * @return             the output as a String
     */
    public String runAppleScriptFile(String filename, String... params) {
        try {
            String filePath = FileSystemUtils.getKeepasteDirectory().concat("/").concat(filename);
            File scriptFile = new File(filePath);
            if (!scriptFile.exists()) {
                try (InputStream inputStream = KeepsManager.class.getResourceAsStream("/scripts/mac/".concat(filename))) {
                    if (inputStream != null) {
                        String fileContents = new String(inputStream.readAllBytes());
                        try (FileWriter writer = new FileWriter(filePath)) {
                            log.info("Saved ".concat(filePath));
                            writer.write(fileContents);
                        }
                    }
                }
            }

            String command = "osascript ".concat(scriptFile.getAbsolutePath());

            for (String param : params) {
                command = command.concat(" \"").concat(param).concat("\"");
            }

            List<String> output = Application.getContext().getKeepExecutionManager().executeCommandWithDefaultPath(command);
            if (!output.isEmpty()) {
                return output.get(0);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error(String.format("Failed to run apple script file [%s]", filename), e);
        }
        return null;
    }

    /**
     * Will delete the used applescript files from the .keepaste folder in order to keep those refreshed, if any changes
     * were done to them between versions.
     */
    private void delAppleScriptFilesForRefresh() {
        FileSystemUtils.deleteFile(FileSystemUtils.getKeepasteDirectory().concat("/").concat(GET_TOP_MOST_WINDOW_APPLESCRIPT_FILENAME));
    }

}

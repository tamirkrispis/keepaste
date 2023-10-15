/**
 * Keepaste - The keep and paste program (http://www.keepaste.com)
 * Copyright (C) 2023 Tamir Krispis
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.keepaste.logic.managers.window;

import com.keepaste.logic.Application;
import com.keepaste.logic.managers.KeepsManager;
import com.keepaste.logic.models.WindowInformation;
import com.keepaste.logic.utils.FileSystemUtils;
import com.keepaste.logic.utils.KeyboardUtils;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class is Mac's {@link WindowManager}, it holds relevant methods related to windows management in Mac's OS's.
 */
@Log4j2
public final class MacWindowsManager extends BaseWindowManager implements WindowManager {
    public static final String GET_TOP_MOST_WINDOW_APPLESCRIPT_FILENAME = "GetTopMostWindow.applescript";
    private String lastTopMostWindowResult;

    public MacWindowsManager() {
        delAppleScriptFilesForRefresh();
    }

    @Override
    public WindowInformation getActiveWindow() {
        String topMostWindowResult = null;
        try {
            int tries = 3;
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
                                .processId(Integer.parseInt(activeWindowSegments[3]))
                                .build();

                    }
                    lastTopMostWindowResult = topMostWindowResult;
                }
                Thread.sleep(150);
                tries--;
            } while (tries > 0);
        } catch (InterruptedException ex) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            log.error(String.format("Failed to get active window, top most window=[%s]", topMostWindowResult == null ? "null" : topMostWindowResult), ex);
        }
        return null;
    }

    @Override
    public void paste() {
        log.debug("Pasting using osascript for CMD+V (Apple)");
        try {
            // this is commented out as it doesn't work well when the command is on one language (English) and the operating system input is set to be in another language (such as Hebrew)
            // so shifted to use cmd+V
            //            Application.getContext().getKeepExecutionManager().executeCommand("osascript -e 'tell application \"System Events\" to keystroke \"v\" using command down'");
            cmdV();
            TimeUnit.MILLISECONDS.sleep(50);
//        } catch (IOException e) {
//            log.error("Failed to paste for mac", e);
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
            Application.getContext().getKeepExecutionManager().executeCommand("osascript -e 'tell application \"System Events\" to key code 118 using control down'");
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
            String filePath = FileSystemUtils.getHomeDirectory().concat("/").concat(filename);
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

            List<String> output = Application.getContext().getKeepExecutionManager().executeCommand(command);
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
     * Will delete the used applescript files from the .keepaste folder in order to keep those refreshed, if any changes were done to them between versions.
     */
    private void delAppleScriptFilesForRefresh() {
        FileSystemUtils.deleteFile(FileSystemUtils.getHomeDirectory().concat("/").concat(GET_TOP_MOST_WINDOW_APPLESCRIPT_FILENAME));
    }

    /**
     * imitating a cmd+v press for pasting on Mac.
     */
    private void cmdV() {
        log.debug("Robot pressing CMD+V (pasting on mac)");
        KeyboardUtils.cmdV();
    }
}

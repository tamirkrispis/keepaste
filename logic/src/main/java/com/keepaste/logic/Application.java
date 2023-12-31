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

package com.keepaste.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.formdev.flatlaf.FlatLightLaf;
import com.keepaste.gui.DialogWelcome;
import com.keepaste.logic.controllers.ControllerActiveWindow;
import com.keepaste.logic.controllers.ControllerLookAndFeel;
import com.keepaste.logic.controllers.ControllerTopMenu;
import com.keepaste.logic.controllers.ControllerTree;
import com.keepaste.logic.managers.KeepExecutionManager;
import com.keepaste.logic.managers.KeepsManager;
import com.keepaste.logic.managers.SettingsManager;
import com.keepaste.logic.managers.window.WindowManager;
import com.keepaste.logic.managers.window.MacWindowsManager;
import com.keepaste.logic.managers.window.WindowsWindowsManager;
import com.keepaste.logic.models.ModelActiveWindow;
import com.keepaste.logic.models.ModelSettings;
import com.keepaste.logic.models.ModelTree;
import com.keepaste.logic.utils.GuiUtils;
import com.keepaste.logic.utils.ImagesUtils;
import com.keepaste.logic.utils.OperatingSystemUtils;
import com.keepaste.logic.utils.WebUtils;
import com.keepaste.logic.views.ViewActiveWindow;
import com.keepaste.logic.views.ViewLookAndFeel;
import com.keepaste.logic.views.ViewTopMenu;
import com.keepaste.logic.views.ViewTree;
import lombok.Getter;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.keepaste.gui.Gui;
import lombok.extern.log4j.Log4j2;
import org.apache.maven.artifact.versioning.ComparableVersion;

/**
 * This class holds the main entry point for the application and holds a reference to the {@code Context}.
 */
@Log4j2
public final class Application {

    public static final int APP_MIN_WIDTH = 320;
    public static final int LOCATION_MARGIN = 50;
    @Getter
    private static Context context;

    private Application() {
        // private constructor to prevent instantiation
    }

    /**
     * Main entry point for the application.
     *
     * @param args the command args
     */
    public static void main(String[] args) {
        try {
            // checking if there is a new release available
            checkForNewRelease();

            // Load settings from disk or get default instance
            ModelSettings modelSettings = SettingsManager.getModelSettings();

            // set look and feel based on settings
            setInitialLookAndFeel(modelSettings);

            // generating app context to be used
            context = generateContext(modelSettings);

            // initializing and displaying the GUI
            initGui();
            
        } catch (Exception ex) {
            log.error("Failed to start the application", ex);
        }
    }

    private static void initGui() {
        // Create and display the form
        EventQueue.invokeLater(() -> {
            try {
                Gui gui = context.getGui();
                // setting minimum size for windows
                if (OperatingSystemUtils.getOperatingSystemType() == OperatingSystemUtils.OperatingSystemType.WINDOWS) {
                    gui.setMinimumSize(new Dimension(APP_MIN_WIDTH, gui.getHeight()));
                }

                gui.setIconImage(getLogoImage());
                gui.labelBackground.setVisible(false);

                // Intercepted window shown on status bar
                ViewActiveWindow viewActiveWindow = new ViewActiveWindow(gui.labelTargetWindow, gui.labelTargetWindowTitle);
                new ControllerActiveWindow(context.getModelActiveWindow(), viewActiveWindow);

                // Top menu bar
                ViewTopMenu viewTopMenu = new ViewTopMenu(gui.menuItemMain, gui.menuItemAbout, gui.menuItemHeart);
                viewTopMenu.initUpperMenuBar();
                new ControllerTopMenu(context.getModelActiveWindow(), viewActiveWindow, viewTopMenu.getLockingMenuItem());

                // Tree
                ViewTree viewTree = new ViewTree();
                ModelTree modelTree = new ModelTree();
                new ControllerTree(modelTree, viewTree);

                // look and feel (theme) controller
                ViewLookAndFeel viewLookAndFeel = new ViewLookAndFeel(viewTree);
                new ControllerLookAndFeel(context.getModelSettings(), viewLookAndFeel);

                final Toolkit toolkit = Toolkit.getDefaultToolkit();
                final Dimension screenSize = toolkit.getScreenSize();

                // do not change the order of these two next lines, setLocationRelativeTo sets the window in the center,
                // but allows ctrl+f4 on Mac, while the second line set the window on a specific position.
                // having those reversed will cause ctrl+f4 which is being used to switch between windows to stop working
                // on always on top mode.
                gui.setLocationRelativeTo(null);
                gui.setLocation(
                        (int) screenSize.getWidth() - gui.getWidth() - LOCATION_MARGIN,
                        (int) screenSize.getHeight() - gui.getHeight() - LOCATION_MARGIN);

                gui.setAlwaysOnTop(context.getModelSettings().isAlwaysOnTop());

                gui.setVisible(true);
                context.getModelSettings().setLookAndFeel(context.getModelSettings().getTheme());
                log.info("Gui initialized");

                if (isFirstTimeRunning()) {
                    // this is the first time keepaste runs on this machine, showing the welcome dialog
                    DialogWelcome dialogWelcome = new DialogWelcome(gui, true);
                    GuiUtils.initHyperlinkLabel(dialogWelcome.labelTutorial, "https://www.keepaste.com/tutorial.htm");
                    GuiUtils.initHyperlinkLabel(dialogWelcome.labelKeepsLibrary, "https://github.com/tamirkrispis/keeps-library");
                    GuiUtils.showDialogOnCenterScreen(dialogWelcome);
                }
            } catch (Exception ex) {
                log.error("Failed to initialize gui", ex);
                System.exit(1);
            }

            context.startWindowInterceptorRunner();
        });
    }

    private static Image getLogoImage() {
        return ImagesUtils.getImage("/logo-white.png");
    }

    private static boolean isFirstTimeRunning() {
        return !new File(context.getKeepsManager().getKeepsFilePathString()).exists();
    }

    private static Context generateContext(ModelSettings modelSettings) {
        return new Context(
                new Gui(),
                getWindowManager(),
                modelSettings,
                new KeepsManager(),
                new KeepExecutionManager(),
                new ModelActiveWindow());
    }

    private static void setInitialLookAndFeel(ModelSettings modelSettings) {
        try {
            FlatLightLaf.setup();
            UIManager.setLookAndFeel(modelSettings.getTheme());
            UIManager.put("Component.focusWidth", 0);
            UIManager.put("TitlePane.iconSize", new Dimension(20, 20));
            UIManager.put("MenuItem.minimumIconSize", 20);
            UIManager.put("Button.arc", 999);
            UIManager.put("Component.arc", 999);
            UIManager.put("ProgressBar.arc", 999);
            UIManager.put("TextComponent.arc", 999);
            UIManager.put("Menu.iconSize", new Dimension(20, 20));
            UIManager.put("MenuBar.itemMargins", new Insets(0, 2, 0, 2));

        } catch (Exception ex) {
            log.error("Failed to init ui look and feel", ex);
        }
    }


    private static WindowManager getWindowManager() {
        switch (OperatingSystemUtils.getOperatingSystemType()) {
            case WINDOWS:
                return new WindowsWindowsManager();
            case MAC:
            case LINUX:
            case OTHER:
            default:
                return new MacWindowsManager();
        }
    }

    private static void checkForNewRelease() {
        if (WebUtils.isInternetAvailable()) {
            try {
                String currentVersionStr = Application.getContext().getVersion();
                if (!currentVersionStr.endsWith("-SNAPSHOT")) {
                    ComparableVersion latestVersion = new ComparableVersion(getLatestVersion());
                    ComparableVersion currentVersion = new ComparableVersion(currentVersionStr);
                    if (latestVersion.compareTo(currentVersion) > 0) {
                        var dialogResult = JOptionPane.showConfirmDialog(getContext().getGui(),
                                String.format("You're using version [%s] while there is a newer version [%s], would you like "
                                        + "to go to https://www.keepaste.com to download it now?", currentVersion, latestVersion),
                                "New keepaste version is available", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (dialogResult == JOptionPane.YES_OPTION) {
                            WebUtils.browseTo("https://www.keepaste.com/#download");
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("Failed to check for new releases against GitHub", ex);
            }
        }
    }

    private static String getLatestVersion() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String gitHubLatestReleaseURL = "https://api.github.com/repos/tamirkrispis/keepaste/releases/latest";
        JsonNode jsonResponse = objectMapper.readTree(new URL(gitHubLatestReleaseURL));
        return jsonResponse.get("name").asText();
    }
}


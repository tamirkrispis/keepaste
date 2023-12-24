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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.keepaste.logic.models.SettingsToFile;
import com.keepaste.logic.utils.FileSystemUtils;
import com.keepaste.logic.models.ModelSettings;
import lombok.extern.log4j.Log4j2;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * This utility class is for managing the {@code ModelSettings}.
 */
@Log4j2
public final class SettingsManager {

    /**
     * private constructor as this is a utility class.
     */
    private SettingsManager() {
        // do nothing
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final File SETTINGS_FILE = new File(FileSystemUtils.getKeepasteDirectory().concat("/settings.json"));

    /**
     * Will load settings from file or create and return default one if not exists or there is an issue loading it.
     *
     * @return a {@code ModelSettings}
     */
    public static ModelSettings getModelSettings() {
        ModelSettings modelSettings = loadSettingsFromFile();

        // in case there is an issue loading the settings file, create a default one instead
        if (modelSettings == null) {
            SettingsManager.createDefaultSettingsFile();
            modelSettings = SettingsManager.loadSettingsFromFile();
        }

        if (modelSettings == null) {
            modelSettings = SettingsManager.getDefaultModelSettings();
        }
        return modelSettings;
    }
    

    /**
     * Will save the given {@code ModelSettings} to a file.
     *
     * @param settings the {@code ModelSettings} to persis
     */
    public static void saveSettingsToFile(ModelSettings settings) {
        validateSettingsFileExists();
        try {

            SettingsToFile settingsToFile = SettingsToFile.builder()
                    .themeClassName(settings.getTheme().getClass().getName())
                    .pressEnterAfterPaste(settings.isPressEnterAfterPaste())
                    .focusOnWindowAndPaste(settings.isFocusOnWindowAndPaste())
                    .copyToClipboard(settings.isCopyToClipboard())
                    .alwaysOnTop(settings.isAlwaysOnTop())
                    .path(settings.getPath())
                    .build();

            MAPPER.writeValue(SETTINGS_FILE, settingsToFile);
        } catch (IOException e) {
            log.error(String.format("Failed to save settings [%s] to file [%s]", settings, SETTINGS_FILE.getPath()), e);
        }
    }

    /**
     * Will create a default settings file with initial default values.
     */
    public static void createDefaultSettingsFile() {
        SettingsToFile settingsToFile = getDefaultSettings();
        saveSettingsToFile(settingsToFile);
    }

    /**
     * Will get the default {@code ModelSettings} once one of the user is not present (persisted already).
     *
     * @return the default {@code ModelSettings}
     */
    public static ModelSettings getDefaultModelSettings() {
        return parseSettingsToFileToModelSettings(getDefaultSettings());
    }

    /**
     * Will load settings from a file.
     *
     * @return {@code ModelSettings}
     */
    private static ModelSettings loadSettingsFromFile() {
        validateSettingsFileExists();
        try {
            log.debug("Loading settings file [{}]", SETTINGS_FILE.getPath());
            SettingsToFile settingsToFile = MAPPER.readValue(SETTINGS_FILE, SettingsToFile.class);
            return parseSettingsToFileToModelSettings(settingsToFile);
        } catch (IOException e) {
            log.error(String.format("Failed to load settings from file [%s]", SETTINGS_FILE), e);
        }
        return null;
    }

    /**
     * Will parse a model representing the file - {@code SettingsToFile} to a genuine {@code ModelSettings}.
     *
     * @return a {@code ModelSettings}
     */
    private static ModelSettings parseSettingsToFileToModelSettings(SettingsToFile settingsToFile) {
        LookAndFeel lookAndFeel = new FlatMacDarkLaf();
        if (settingsToFile.getThemeClassName().equals(FlatMacLightLaf.class.getName())) {
            lookAndFeel = new FlatMacLightLaf();
        }
        return ModelSettings.builder()
                .copyToClipboard(settingsToFile.isCopyToClipboard())
                .focusOnWindowAndPaste(settingsToFile.isFocusOnWindowAndPaste())
                .pressEnterAfterPaste(settingsToFile.isPressEnterAfterPaste())
                .theme(lookAndFeel)
                .alwaysOnTop(settingsToFile.isAlwaysOnTop())
                .path(settingsToFile.getPath())
                .build();
    }

    /**
     * Will save the given {@code SettingsToFile} to a file.
     *
     * @param settings the settings to persist
     */
    private static void saveSettingsToFile(SettingsToFile settings) {
        try {
            log.debug("Saving settings [{}] to file [{}]", settings, SETTINGS_FILE);
            MAPPER.writeValue(SETTINGS_FILE, settings);
        } catch (IOException e) {
            log.error(String.format("Failed to save settings [%s] to file [%s]", settings, SETTINGS_FILE.getPath()), e);
        }
    }


    private static void validateSettingsFileExists() {
        if (!SETTINGS_FILE.exists()) {
            // setting and saving default settings
            log.debug("Settings file [{}] doesn't exist, creating one", SETTINGS_FILE.getPath());
            createDefaultSettingsFile();
        }
    }

    private static SettingsToFile getDefaultSettings() {
        return SettingsToFile.builder()
                .copyToClipboard(true)
                .focusOnWindowAndPaste(true)
                .pressEnterAfterPaste(true)
                .themeClassName("FlatMacDarkLaf")
                .alwaysOnTop(true)
                .path(System.getenv("PATH"))
                .build();
    }
}

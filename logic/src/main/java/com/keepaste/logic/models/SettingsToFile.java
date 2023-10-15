package com.keepaste.logic.models;

import lombok.*;

/**
 * This is a simplified class is for saving the settings as defined in {@link ModelSettings} as Json on a file.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SettingsToFile {
    private boolean copyToClipboard;
    private boolean focusOnWindowAndPaste;
    private boolean pressEnterAfterPaste;
    private String themeClassName;
    private boolean alwaysOnTop;
}

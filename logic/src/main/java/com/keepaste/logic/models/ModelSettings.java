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

package com.keepaste.logic.models;

import lombok.Builder;
import lombok.Getter;

import javax.swing.*;

/**
 * This class represents the settings model.
 */
@Builder
@Getter
public class ModelSettings extends BaseModel {
    public static final String LINE_SEPARATOR = "line.separator";
    // execution flow - if to copy the Keep to the Clipboard.
    private boolean copyToClipboard;
    // execution flow - if to focus on the active window and paste the Keep's phrase.
    private boolean focusOnWindowAndPaste;
    // execution flow - if to press the 'Enter' key after pasting, to execute the phrase.
    private boolean pressEnterAfterPaste;
    // the set GUI theme
    private LookAndFeel theme;
    // if the window should be always on top
    private boolean alwaysOnTop;

    /**
     * If to copy the Keep to the Clipboard.
     *
     * @param value the value
     */
    public void setCopyToClipboard(final boolean value) {
        this.copyToClipboard = value;
        updateAllObservers(this);
    }

    /**
     * If to press the 'Enter' key after pasting, to execute the phrase.
     *
     * @param value the value
     */
    public void setPressEnterAfterPaste(final boolean value) {
        this.pressEnterAfterPaste = value;
        updateAllObservers(this);
    }

    /**
     * If to focus on the active window and paste the Keep's phrase.
     *
     * @param value the value
     */
    public void setFocusOnWindowAndPaste(final boolean value) {
        this.focusOnWindowAndPaste = value;
        updateAllObservers(this);
    }

    /**
     * The set GUI theme.
     *
     * @param lookAndFeel the value
     */
    public void setLookAndFeel(LookAndFeel lookAndFeel) {
        this.theme = lookAndFeel;
        updateAllObservers(this);
    }

    /**
     * If the window should be always on top
     * @param value the value
     */
    public void setAlwaysOnTop(final boolean value) {
        this.alwaysOnTop = value;
        updateAllObservers(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("copyToClipboard=".concat(String.valueOf(copyToClipboard)));
        sb.append(System.getProperty(LINE_SEPARATOR));
        sb.append("focusOnTargetWindowAndPaste=".concat(String.valueOf(focusOnWindowAndPaste)));
        sb.append(System.getProperty(LINE_SEPARATOR));
        sb.append("pressEnterAfterPaste=".concat(String.valueOf(pressEnterAfterPaste)));
        sb.append(System.getProperty(LINE_SEPARATOR));
        sb.append("theme=".concat(getTheme().getClass().getName()));
        sb.append(System.getProperty(LINE_SEPARATOR));
        return sb.toString();
    }
}

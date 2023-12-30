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

package com.keepaste.logic.utils;

import lombok.extern.log4j.Log4j2;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * This class is a utility class for anything related to clipboard.
 */
@Log4j2
public final class ClipboardUtils {
    static final Clipboard CLIP = Toolkit.getDefaultToolkit().getSystemClipboard();
    private ClipboardUtils() {

    }

    /**
     * Will return the current value from the clipboard.
     *
     * @return the current value from the clipboard.
     */
    public static Transferable getValue() {
        return CLIP.getContents(null);
    }

    /**
     * Will set a value to the clipboard.
     *
     * @param value the value to be set
     */
    public static void setValue(String value) {
        log.debug("Setting value of [{}] into the clipboard", value);
        StringSelection commandStringSelection = new StringSelection(value);
        CLIP.setContents(commandStringSelection, commandStringSelection);
    }
}

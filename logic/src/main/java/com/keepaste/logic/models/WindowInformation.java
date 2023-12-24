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

package com.keepaste.logic.models;

import com.sun.jna.platform.win32.WinDef;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * This class holds information of another application's window.
 */
@Getter
//@ToString
@Builder
@EqualsAndHashCode
public class WindowInformation {
    private int processId;
    private String text;
    private String app;
    private WinDef.HWND hwnd;
    private int left;
    private int right;
    private int top;
    private int bottom;

    @Override
    public String toString() {
        return String.format("App=[%s], text=[%s], process id=[%s]", app, text, processId);
    }
}

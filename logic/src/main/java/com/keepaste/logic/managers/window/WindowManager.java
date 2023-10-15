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

import com.keepaste.logic.models.WindowInformation;
import lombok.NonNull;

/**
 * This interface is for the different window managers for each operating system.
 */
public interface WindowManager {
    /**
     * Will get the currently front-most and focused window from the operating system.
     *
     * @return a {@link WindowInformation} containing relevant information about the window
     */
    WindowInformation getActiveWindow();

    /**
     * Will switch to the window before pasting the executed Keep.
     *
     * @param windowContext the {@link WindowInformation} as the context of the window
     * @return  true if succeeded, false if failed
     */
    boolean focusOnActiveWindow(@NonNull WindowInformation windowContext);

    /**
     * Paste from clipboard.
     */
    void paste();
}

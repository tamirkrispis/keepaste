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

import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

/**
 * This class represents the active window model, it holds the intercepted, locked and active windows in context.
 */
@ToString
@Log4j2
public class ModelActiveWindow extends BaseModel {

    // the current intercepted window by the window interceptor.
    @Getter
    WindowInformation interceptedWindow = null;
    // the set active window to work with when executing a Keep.
    @Getter
    WindowInformation activeWindow = null;
    // the locked on window if this option is choosed.
    @Getter
    WindowInformation lockedOnWindow = null;

    public void setInterceptedWindow(WindowInformation window) {
        if (window != interceptedWindow) {
            this.interceptedWindow = window;
            updateAllObservers(this);
        }
    }

    public void setActiveWindow(WindowInformation window) {
        log.debug("Active window set to [{}]", window);
        this.activeWindow = window;
    }

    public void lockOnActiveWindow() {
        lockedOnWindow = getActiveWindow();
    }

    public void unLockFromWindow() {
        lockedOnWindow = null;
    }

    public boolean isLockedOnWindow() {
        return lockedOnWindow != null;
    }
}

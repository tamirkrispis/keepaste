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

package com.keepaste.logic.controllers;

import com.keepaste.logic.common.BaseSubject;
import com.keepaste.logic.common.Observer;
import com.keepaste.logic.common.Subject;
import com.keepaste.logic.models.Model;
import com.keepaste.logic.models.ModelActiveWindow;
import com.keepaste.logic.models.WindowInformation;
import com.keepaste.logic.views.ViewActiveWindow;
import lombok.NonNull;

/**
 * This class will control the event whenever a new active window is intercepted.
 * The controller class is an {@link Observer}, it listens on changes coming from the Model.
 * And, it is also a {@link Subject}, after doing its logic, it will call updateObserver on the view which observe the controller.
 */
public class ControllerActiveWindow extends BaseSubject implements Observer {

    public ControllerActiveWindow(@NonNull final ModelActiveWindow modelActiveWindow,
                                  @NonNull final ViewActiveWindow viewActiveWindow) {
        // ModelActiveWindow --> ControllerActiveWindow --> ViewActiveWindow
        // listening on the model
        modelActiveWindow.registerObserver(this);
        // view listens on this
        this.registerObserver(viewActiveWindow);
    }

    @Override
    public void updateObserver(Model model) {
        if (model != null) {
            ModelActiveWindow modelActiveWindow = (ModelActiveWindow) model;
            WindowInformation interceptedWindow = modelActiveWindow.getInterceptedWindow();
            WindowInformation activeWindow = modelActiveWindow.getActiveWindow();

            if (!modelActiveWindow.isLockedOnWindow() && interceptedWindow != null && !interceptedWindow.equals(activeWindow)) {
                modelActiveWindow.setActiveWindow(interceptedWindow);
                // update the view
                updateAllObservers(model);
            }
        }
    }
}

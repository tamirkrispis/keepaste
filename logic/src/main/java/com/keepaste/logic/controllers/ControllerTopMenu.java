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

package com.keepaste.logic.controllers;

import com.keepaste.logic.actionlisteners.topmenu.LockingActionListener;
import com.keepaste.logic.common.BaseSubject;
import com.keepaste.logic.common.Observer;
import com.keepaste.logic.models.Model;
import com.keepaste.logic.models.ModelActiveWindow;
import com.keepaste.logic.views.ViewActiveWindow;
import lombok.NonNull;
import javax.swing.*;

/**
 * This class is a {@code BaseController} for the locking menu item.
 */
public class ControllerTopMenu extends BaseSubject implements Observer {

    /**
     * Constructor.
     *
     * @param modelActiveWindow {@code ModelActiveWindow}
     * @param viewActiveWindow  {@code ViewActiveWindow}
     * @param lockingMenuItem   the locking {@code JMenuItem}
     */
    public ControllerTopMenu(@NonNull final ModelActiveWindow modelActiveWindow,
                             @NonNull final ViewActiveWindow viewActiveWindow,
                             final JMenuItem lockingMenuItem) {
        if (lockingMenuItem != null) {
            lockingMenuItem.addActionListener(new LockingActionListener(modelActiveWindow, viewActiveWindow));
        }
    }

    @Override
    public void updateObserver(Model model) {
        updateAllObservers(model);
    }
}

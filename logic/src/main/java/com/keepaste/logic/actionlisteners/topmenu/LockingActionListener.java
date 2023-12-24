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

package com.keepaste.logic.actionlisteners.topmenu;

import com.keepaste.logic.Application;
import com.keepaste.logic.views.ViewActiveWindow;
import com.keepaste.logic.models.ModelActiveWindow;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is an ActionListener for menu item - locking on window (available only on Windows as we got a handle to that window).
 */
@Log4j2
public class LockingActionListener implements ActionListener {

    private final ModelActiveWindow modelActiveWindow;
    private final ViewActiveWindow viewActiveWindow;

    /**
     * Constructor.
     *
     * @param modelActiveWindow {@code ModelActiveWindow}
     * @param viewActiveWindow  {@code ViewActiveWindow}
     */
    public LockingActionListener(@NonNull final ModelActiveWindow modelActiveWindow,
                                 @NonNull final ViewActiveWindow viewActiveWindow) {
        this.modelActiveWindow = modelActiveWindow;
        this.viewActiveWindow = viewActiveWindow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("TopMenu - Toggling locking, the locking menu item was pressed");
        JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();
        if (menuItem.isSelected()) {
            if (modelActiveWindow.getActiveWindow() != null) {
                if (modelActiveWindow.getLockedOnWindow() == null) {
                    log.debug("TopMenu - Locking on [{}]", modelActiveWindow.getActiveWindow());
                    Application.getContext().stopWindowInterceptorRunner();
                    modelActiveWindow.lockOnActiveWindow();
                    viewActiveWindow.updateObserver(modelActiveWindow);
                    log.debug("TopMenu - Locking on [{}]", modelActiveWindow.getActiveWindow());
                }
            } else {
                menuItem.setSelected(false);
                JOptionPane.showMessageDialog(Application.getContext().getGui().getContentPane(),
                        "Please select a window first by clicking on it", "No active window", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            log.debug("TopMenu - Unlocking from [{}]", modelActiveWindow.getLockedOnWindow());
            modelActiveWindow.unLockFromWindow();
            Application.getContext().startWindowInterceptorRunner();
            viewActiveWindow.updateObserver(modelActiveWindow);
            log.debug("TopMenu - Unlocked from [{}]", modelActiveWindow.getLockedOnWindow());
        }
    }
}

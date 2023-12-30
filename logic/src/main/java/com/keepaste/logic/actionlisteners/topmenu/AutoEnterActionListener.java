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
import com.keepaste.logic.common.Constants;
import com.keepaste.logic.utils.GuiUtils;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is an ActionListener for menu item - flow - auto-enter.
 */
@Log4j2
public class AutoEnterActionListener implements ActionListener {

    private final JCheckBoxMenuItem copyToClipboardCheckboxMenuItem;
    private final JCheckBoxMenuItem focusOnTargetWindowCheckboxMenuItem;

    /**
     * Constructor.
     *
     * @param copyToClipboardCheckboxMenuItem       copy to clipboard checkbox UI element
     * @param focusOnTargetWindowCheckboxMenuItem   copy to clipboard checkbox UI element
     */
    public AutoEnterActionListener(@NonNull final JCheckBoxMenuItem copyToClipboardCheckboxMenuItem,
                                   @NonNull final JCheckBoxMenuItem focusOnTargetWindowCheckboxMenuItem) {
        this.copyToClipboardCheckboxMenuItem = copyToClipboardCheckboxMenuItem;
        this.focusOnTargetWindowCheckboxMenuItem = focusOnTargetWindowCheckboxMenuItem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("TopMenu - Toggling auto enter");
        JCheckBoxMenuItem checkBoxMenuItem = (JCheckBoxMenuItem) e.getSource();
        Application.getContext().getModelSettings().setPressEnterAfterPaste(checkBoxMenuItem.isSelected());
        Application.getContext().getModelSettings().setCopyToClipboard(true);
        copyToClipboardCheckboxMenuItem.setSelected(true);
        Application.getContext().getModelSettings().setFocusOnWindowAndPaste(true);
        focusOnTargetWindowCheckboxMenuItem.setSelected(true);
        Application.getContext().startWindowInterceptorRunner();

        if (checkBoxMenuItem.isSelected()) {
            GuiUtils.showTargetWindowLabelMessage(Constants.FULL_ON_MODE, 1);
        } else {
            GuiUtils.showTargetWindowLabelMessage(Constants.PASTE_BUT_DO_NOT_RUN_MODE, 1);
        }
        log.debug("TopMenu - full on mode");
    }
}

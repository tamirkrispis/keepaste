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

package com.keepaste.logic.actionlisteners.topmenu;

import com.keepaste.logic.Application;
import com.keepaste.logic.common.Constants;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is an ActionListener for menu item - copy to clipboard.
 */
@Log4j2
public class CopyToClipboardActionListener implements ActionListener {

    private final JCheckBoxMenuItem autoEnterCheckboxMenuItem;
    private final JCheckBoxMenuItem focusOnTargetWindowCheckboxMenuItem;

    /**
     * Constructor.
     *
     * @param autoEnterCheckboxMenuItem             the auto-enter {@link JCheckBoxMenuItem}
     * @param focusOnTargetWindowCheckboxMenuItem   the focus on target window {@link JCheckBoxMenuItem}
     */
    public CopyToClipboardActionListener(@NonNull final JCheckBoxMenuItem autoEnterCheckboxMenuItem,
                                         @NonNull final JCheckBoxMenuItem focusOnTargetWindowCheckboxMenuItem) {
        this.autoEnterCheckboxMenuItem = autoEnterCheckboxMenuItem;
        this.focusOnTargetWindowCheckboxMenuItem = focusOnTargetWindowCheckboxMenuItem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("TopMenu - Toggling copy to clipboard");
        ((JCheckBoxMenuItem) e.getSource()).setSelected(true);
        Application.getContext().getModelSettings().setCopyToClipboard(true);
        Application.getContext().stopWindowInterceptorRunner();
        Application.getContext().getModelSettings().setFocusOnWindowAndPaste(false);
        Application.getContext().getModelSettings().setPressEnterAfterPaste(false);
        focusOnTargetWindowCheckboxMenuItem.setSelected(false);
        autoEnterCheckboxMenuItem.setSelected(false);
        Application.getContext().getGui().labelTargetWindow.setText(Constants.ONLY_COPY_MODE);
        log.debug("TopMenu - Only-copy-mode");
    }
}

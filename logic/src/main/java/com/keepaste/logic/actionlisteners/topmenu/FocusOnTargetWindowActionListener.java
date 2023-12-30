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
 * This class is an ActionListener for menu item - flow - focus on target window.
 */
@Log4j2
public class FocusOnTargetWindowActionListener  implements ActionListener {
    private final JCheckBoxMenuItem copyToClipboardCheckboxMenuItem;
    private final JCheckBoxMenuItem autoEnterCheckboxMenuItem;

    /**
     * Constructor.
     *
     * @param copyToClipboardCheckboxMenuItem   copy-to-clipboard {@code JCheckBoxMenuItem}
     * @param autoEnterCheckboxMenuItem         auto-enter {@code JCheckBoxMenuItem}
     */
    public FocusOnTargetWindowActionListener(@NonNull final JCheckBoxMenuItem copyToClipboardCheckboxMenuItem,
                                             @NonNull final JCheckBoxMenuItem autoEnterCheckboxMenuItem) {
        this.copyToClipboardCheckboxMenuItem = copyToClipboardCheckboxMenuItem;
        this.autoEnterCheckboxMenuItem = autoEnterCheckboxMenuItem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("TopMenu - Toggling focus on target window and paste");
        Application.getContext().getModelSettings().setFocusOnWindowAndPaste(true);
        ((JCheckBoxMenuItem) e.getSource()).setSelected(true);
        Application.getContext().getModelSettings().setCopyToClipboard(true);
        copyToClipboardCheckboxMenuItem.setSelected(true);
        Application.getContext().getModelSettings().setPressEnterAfterPaste(false);
        autoEnterCheckboxMenuItem.setSelected(false);
        GuiUtils.showTargetWindowLabelMessage(Constants.PASTE_BUT_DO_NOT_RUN_MODE, 1);
        Application.getContext().startWindowInterceptorRunner();
        log.debug("TopMenu - Focus & paste but do not run mode");
    }
}

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

import com.keepaste.gui.DialogAbout;
import com.keepaste.logic.Application;
import com.keepaste.logic.utils.GuiUtils;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * This class is an ActionListener for menu item - show about.
 */
public class DialogAboutActionListener implements MouseListener {
    DialogAbout dialogAbout;

    @Override
    public void mouseClicked(MouseEvent e) {
        dialogAbout = new DialogAbout(Application.getContext().getGui(), true);
        initDialog();
        GuiUtils.showDialogOnCenterScreen(dialogAbout);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // do nothing
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // do nothing
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // do nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // do nothing
    }

    private void initDialog() {
        GuiUtils.initHyperlinkLabel(dialogAbout.labelWebsite, "http://www.keepaste.com");
        GuiUtils.initHyperlinkLabel(dialogAbout.labelGithub, "https://github.com/tamirkrispis/keepaste");
        GuiUtils.initHyperlinkLabel(dialogAbout.labelIcons8, "https://icons8.com/");
        GuiUtils.initHyperlinkLabel(dialogAbout.labelKeepsLibrary, "https://github.com/tamirkrispis/keeps-library");
        dialogAbout.labelVersion.setText(String.format("Version %s", Application.getContext().getVersion()));
    }
}

/**
 * Keepaste - The keep and paste program (https://www.keepaste.com)
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

import com.keepaste.gui.DialogHeart;
import com.keepaste.logic.Application;
import com.keepaste.logic.utils.GuiUtils;
import com.keepaste.logic.utils.WebUtils;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * This class is an ActionListener for menu item - heart/love keepaste.
 */
public class DialogHeartActionListener implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
        if (WebUtils.isInternetAvailable()) {
            WebUtils.browseTo("http://www.keepaste.com/love.htm");
        } else {
            GuiUtils.showDialogOnCenterScreen(new DialogHeart(Application.getContext().getGui(), true));
        }
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
}

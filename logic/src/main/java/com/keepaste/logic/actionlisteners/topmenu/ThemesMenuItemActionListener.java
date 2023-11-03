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

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.keepaste.logic.Application;
import lombok.extern.log4j.Log4j2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is an ActionListener for menu item - themes (dark/light).
 */
@Log4j2
public class ThemesMenuItemActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("TopMenu - Setting look and feel (theme) as [{}]", e.getActionCommand());
        switch (e.getActionCommand()) {
            case "Light":
                var lightTheme = new FlatMacLightLaf();
                if (!Application.getContext().getModelSettings().getTheme().getName().equals(lightTheme.getName())) {
                    Application.getContext().getModelSettings().setLookAndFeel(new FlatMacLightLaf());
                }
                break;
            default:
            case "Dark":
                var darkTheme = new FlatMacDarkLaf();
                if (!Application.getContext().getModelSettings().getTheme().getName().equals(darkTheme.getName())) {
                    Application.getContext().getModelSettings().setLookAndFeel(new FlatMacDarkLaf());
                }
                break;
        }
        log.debug("TopMenu - Look and feel (theme) is now set as [{}]", e.getActionCommand());
    }
}

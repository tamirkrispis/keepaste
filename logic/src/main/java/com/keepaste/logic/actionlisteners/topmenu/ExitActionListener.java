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

import lombok.extern.log4j.Log4j2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is an ActionListener for menu item - exit.
 */
@Log4j2
public class ExitActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        log.info("TopMenu - Exiting...");
        System.exit(0);
    }
}

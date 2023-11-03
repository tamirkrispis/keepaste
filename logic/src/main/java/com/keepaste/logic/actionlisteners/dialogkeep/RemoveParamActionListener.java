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

package com.keepaste.logic.actionlisteners.dialogkeep;

import com.keepaste.logic.views.KeepParametersTableModel;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is an {@link ActionListener} for removing a parameter from a Keep on the {@link com.keepaste.gui.DialogKeep}.
 */
@Log4j2
public class RemoveParamActionListener  extends BaseDialogKeepActionListener {


    /**
     * Constructor.
     *
     * @param table the {@link JTable} to show the parameters
     */
    public RemoveParamActionListener(final JTable table) {
        super(table);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // removing the parameter from the Keep dialog
        log.debug("DialogKeep - Removing parameter from DialogKeep");
        if (getTable().getSelectedRow() >= 0) {
            KeepParametersTableModel model = (KeepParametersTableModel) getTable().getModel();
            model.removeRow(getTable().getSelectedRow());
            getTable().updateUI();
        }
        log.debug("DialogKeep - Removed parameter from DialogKeep");
    }
}

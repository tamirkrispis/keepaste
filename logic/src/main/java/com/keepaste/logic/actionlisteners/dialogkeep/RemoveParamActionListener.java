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

package com.keepaste.logic.actionlisteners.dialogkeep;

import com.keepaste.gui.DialogKeep;
import com.keepaste.logic.views.KeepParametersTableModel;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * This class is an {@code ActionListener} for removing a parameter from a Keep on the {@code com.keepaste.gui.DialogKeep}.
 */
@Log4j2
public class RemoveParamActionListener  extends BaseDialogKeepActionListener {

    /**
     * Constructor.
     *
     * @param dialogKeep a {@code DialogKeep}
     */
    public RemoveParamActionListener(final DialogKeep dialogKeep) {
        super(dialogKeep);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // removing the parameter from the Keep dialog
        log.debug("DialogKeep - Removing parameter from DialogKeep");
        JTable tableParams = dialogKeep.tableParams;
        if (tableParams.getSelectedRow() >= 0) {
            KeepParametersTableModel model = (KeepParametersTableModel) tableParams.getModel();
            model.removeRow(tableParams.getSelectedRow());
            tableParams.updateUI();
        }
        log.debug("DialogKeep - Removed parameter from DialogKeep");
    }
}

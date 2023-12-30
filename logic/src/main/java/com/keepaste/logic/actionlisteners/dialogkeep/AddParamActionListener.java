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
import java.awt.event.ActionEvent;

/**
 * This class is an ActionListener for adding a parameter to a Keep on the {@code com.keepaste.gui.DialogKeep}.
 */
@Log4j2
public class AddParamActionListener extends BaseDialogKeepActionListener {

    /**
     * Constructor.
     *
     * @param dialogKeep a {@code DialogKeep}
     */
    public AddParamActionListener(final DialogKeep dialogKeep) {
        super(dialogKeep);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Adding a new parameter to the parameters table on the dialog
        log.debug("DialogKeep - Adding new keep parameter on the Dialog Command");
        var tableParams = dialogKeep.tableParams;
        KeepParametersTableModel model = (KeepParametersTableModel) tableParams.getModel();
        model.addRow(model.getRowCount(), "", "");
        // Scroll to the added row
        int row = model.getRowCount() - 1;
        tableParams.scrollRectToVisible(tableParams.getCellRect(row, 0, true));

        // Request focus on the added row's first cell
        tableParams.requestFocus();
        tableParams.changeSelection(row, 0, false, false);
        tableParams.editCellAt(row, 0);
        tableParams.getEditorComponent().requestFocus();

        // updating the table to show the new row
        tableParams.updateUI();
        log.debug("DialogKeep - Added new keep parameter on the Dialog Command");
    }
}

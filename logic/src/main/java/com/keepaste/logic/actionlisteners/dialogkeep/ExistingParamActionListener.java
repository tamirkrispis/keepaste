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
import com.keepaste.logic.Application;
import com.keepaste.logic.models.KeepParameter;
import com.keepaste.logic.views.KeepParametersTableModel;
import lombok.extern.log4j.Log4j2;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * This class is an ActionListener for showing a list of existing parameters so the user will be able to use any on the
 * current Keep on the {@code com.keepaste.gui.DialogKeep}.
 */
@Log4j2
public class ExistingParamActionListener extends BaseDialogKeepActionListener {

    /**
     * Constructor.
     *
     * @param dialogKeep a {@code DialogKeep}.
     */
    public ExistingParamActionListener(final DialogKeep dialogKeep) {
        super(dialogKeep);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("DialogKeep - Adding a keep parameter on Dialog Command from existing parameters list");
        // showing a list of available parameters
        List<KeepParameter> parameters = Application.getContext().getKeepsManager().getAllUniqueParameters();

        KeepParameter selectedParam = (KeepParameter) JOptionPane.showInputDialog(
                dialogKeep,
                "Choose from the list of existing parameters",
                "Existing parameters",
                JOptionPane.QUESTION_MESSAGE,
                null,
                parameters.toArray(),
                null);
        if (selectedParam != null) {
            KeepParametersTableModel model = (KeepParametersTableModel) dialogKeep.tableParams.getModel();
            model.addRow(model.getRowCount(), selectedParam.getName(), selectedParam.getPhrase());
            dialogKeep.tableParams.updateUI();
        }
        log.debug("DialogKeep - Added [{}] keep parameter on Dialog Command from existing parameters list", selectedParam);
    }
}

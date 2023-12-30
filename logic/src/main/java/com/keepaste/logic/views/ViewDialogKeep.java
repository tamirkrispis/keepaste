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

package com.keepaste.logic.views;

import com.keepaste.gui.DialogKeep;
import com.keepaste.logic.Application;
import com.keepaste.logic.actionlisteners.dialogkeep.AddParamActionListener;
import com.keepaste.logic.actionlisteners.dialogkeep.ExistingParamActionListener;
import com.keepaste.logic.actionlisteners.dialogkeep.OkButtonActionListener;
import com.keepaste.logic.actionlisteners.dialogkeep.RemoveParamActionListener;
import com.keepaste.logic.models.Keep;
import com.keepaste.logic.models.KeepParameter;
import com.keepaste.logic.utils.GuiUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * This View class manages the {@code DialogKeep}.
 */
public class ViewDialogKeep {
    private final DialogKeep dialogKeep = new DialogKeep(Application.getContext().getGui(), true);
    private final List<KeepParameter> editedParameters;

    /**
     * Constructor.
     *
     * @param keep      the {@code Keep} in context
     */
    public ViewDialogKeep(final Keep keep) {
        dialogKeep.setAlwaysOnTop(true);
        dialogKeep.setModal(true);

        dialogKeep.setLocationRelativeTo(Application.getContext().getGui());

        dialogKeep.textName.setText(keep.getTitle());
        dialogKeep.textKeep.setText(keep.getPhrase());
        dialogKeep.textDescription.setText(keep.getDescription());
        dialogKeep.checkNeverPressEnter.setSelected(keep.isNeverPressEnter());

        // parameters
        this.editedParameters = keep.getParameters() == null ? new ArrayList<>() : new ArrayList<>(keep.getParameters());
        dialogKeep.tableParams.setModel(getTableModel());

        dialogKeep.buttonAddParam.addActionListener(new AddParamActionListener(dialogKeep));
        dialogKeep.buttonRemoveParam.addActionListener(new RemoveParamActionListener(dialogKeep));
        dialogKeep.buttonExistingParam.addActionListener(new ExistingParamActionListener(dialogKeep));
        dialogKeep.buttonCancel.addActionListener(e -> dialogKeep.setVisible(false));
        dialogKeep.buttonOK.addActionListener(new OkButtonActionListener(dialogKeep, keep, editedParameters));

        GuiUtils.showDialogOnCenterScreen(dialogKeep);
    }

    private KeepParametersTableModel getTableModel() {
        return new KeepParametersTableModel(editedParameters);
    }
}

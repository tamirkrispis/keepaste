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

package com.keepaste.logic.views;

import com.keepaste.gui.DialogKeep;
import com.keepaste.logic.Application;
import com.keepaste.logic.actionlisteners.dialogkeep.AddParamActionListener;
import com.keepaste.logic.actionlisteners.dialogkeep.ExistingParamActionListener;
import com.keepaste.logic.actionlisteners.dialogkeep.RemoveParamActionListener;
import com.keepaste.logic.models.Keep;
import com.keepaste.logic.models.KeepsGroup;
import com.keepaste.logic.models.KeepParameter;
import com.keepaste.logic.utils.GuiUtils;
import org.apache.commons.lang3.StringUtils;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.util.ArrayList;
import java.util.List;

/**
 * This View class manages the {@link DialogKeep}.
 */
public class ViewDialogKeep {
    private final DialogKeep dialogKeep = new DialogKeep(Application.getContext().getGui(), true);
    private final List<KeepParameter> editedParameters;

    /**
     * Constructor.
     *
     * @param keep      the {@link Keep} in context
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

        dialogKeep.buttonAddParam.addActionListener(new AddParamActionListener(dialogKeep.tableParams));
        dialogKeep.buttonRemoveParam.addActionListener(new RemoveParamActionListener(dialogKeep.tableParams));
        dialogKeep.buttonExistingParam.addActionListener(new ExistingParamActionListener(dialogKeep));

        dialogKeep.buttonCancel.addActionListener(e -> dialogKeep.setVisible(false));

        dialogKeep.buttonOK.addActionListener(e -> {
            if (dialogKeep.tableParams.isEditing()) {
                TableCellEditor cellEditor = dialogKeep.tableParams.getCellEditor(
                        dialogKeep.tableParams.getEditingRow(), dialogKeep.tableParams.getEditingColumn());
                if (cellEditor != null) {
                    cellEditor.stopCellEditing();
                }
            }
            if (StringUtils.isEmpty(dialogKeep.textKeep.getText()) || StringUtils.isEmpty(dialogKeep.textName.getText())) {
                JOptionPane.showMessageDialog(Application.getContext().getGui(),
                        "Both Name and Keep fields are mandatory", "Not enough...", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // validating that there are no duplications in parameter names
            if (editedParameters.stream().map(KeepParameter::getName).distinct().count() < editedParameters.size()) {
                JOptionPane.showMessageDialog(Application.getContext().getGui(),
                        "There are duplications in parameters names, please review");
                return;
            }

            // validating that all command parameters are met
            String commandStr = dialogKeep.textKeep.getText();
            for (KeepParameter parameter : editedParameters) {
                commandStr = commandStr.replace(String.format("<%s>", parameter.getName()), "");
            }
            if (commandStr.contains("<") && commandStr.contains(">")) {
                JOptionPane.showMessageDialog(Application.getContext().getGui(),
                        "Given parameters does not cover all keep parameters, please add missing ones");
                return;
            }

            // also for parameters
            for (KeepParameter parameter : editedParameters) {
                String parameterPhrase = parameter.getPhrase();
                for (KeepParameter otherParameters : editedParameters) {
                    if (otherParameters == parameter) {
                        // validating that a keep parameter doesn't reference itself
                        if (parameterPhrase.contains(String.format("<%s>", parameter.getName()))) {
                            JOptionPane.showMessageDialog(Application.getContext().getGui(),
                                    String.format("The parameter %s cannot use its own name as a command parameter.", parameter.getName()));
                            return;
                        }
                    } else {
                        parameterPhrase = parameterPhrase.replace(String.format("<%s>", otherParameters.getName()), "");
                    }
                }
                int startIndex = parameterPhrase.indexOf("<");
                int endIndex = parameterPhrase.indexOf(">", startIndex);
                if (startIndex > 0 && endIndex > 0) {
                    String missingParamName = parameterPhrase.substring(startIndex + 1, endIndex);
                    JOptionPane.showMessageDialog(Application.getContext().getGui(),
                            String.format("Given parameters does not cover all used parameters (on keep or its parameters),"
                                    + " please add the following missing parameter - %s", missingParamName));
                    return;
                }
            }

            keep.setTitle(dialogKeep.textName.getText().trim());
            keep.setPhrase(dialogKeep.textKeep.getText().trim());
            keep.setDescription(dialogKeep.textDescription.getText().trim());
            keep.setNeverPressEnter(dialogKeep.checkNeverPressEnter.isSelected());
            // getting parameters
            keep.setParameters(editedParameters);
            KeepsGroup rootNode = Application.getContext().getKeepsManager().getRootNode();
            Application.getContext().getKeepsManager().saveKeeps(rootNode);
            dialogKeep.setVisible(false);
        });
        GuiUtils.showDialogOnCenterScreen(dialogKeep);
    }

    private KeepParametersTableModel getTableModel() {
        return new KeepParametersTableModel(editedParameters);
    }
}

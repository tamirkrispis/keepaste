package com.keepaste.logic.actionlisteners.dialogkeep;

import com.keepaste.gui.DialogKeep;
import com.keepaste.logic.Application;
import com.keepaste.logic.models.Keep;
import com.keepaste.logic.models.KeepParameter;
import com.keepaste.logic.models.KeepsGroup;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionEvent;
import java.util.List;

public class OkButtonActionListener extends BaseDialogKeepActionListener {
    private final List<KeepParameter> editedParameters;
    private final Keep keep;

    /**
     * Constructor.
     *
     * @param dialogKeep        a {@code DialogKeep}
     * @param keep              a {@code Keep}
     * @param editedParameters  a list of {@code KeepParameter}
     */
    public OkButtonActionListener(@NonNull final DialogKeep dialogKeep,
                                  @NonNull final Keep keep,
                                  @NonNull final List<KeepParameter> editedParameters) {
        super(dialogKeep);
        this.keep = keep;
        this.editedParameters = editedParameters;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // if any cell is edited, stop/finish editing it
        stopEditing();

        // validating all mandatory fields provided
        if (mandatoryFieldsNotProvided()) return;

        // validating that there are no duplications in parameter names
        if (duplicationsInParamsNames()) return;

        // validating that all command parameters are met
        if (notAllParamsOfCommandProvided()) return;

        // validating that all parameters used in parameters are met
        if (notAllParamsOfParamsProvided()) return;

        keep.setTitle(dialogKeep.textName.getText().trim());
        keep.setPhrase(dialogKeep.textKeep.getText().trim());
        keep.setDescription(dialogKeep.textDescription.getText().trim());
        keep.setNeverPressEnter(dialogKeep.checkNeverPressEnter.isSelected());
        keep.setParameters(editedParameters);

        KeepsGroup rootNode = Application.getContext().getKeepsManager().getRootNode();
        Application.getContext().getKeepsManager().saveKeeps(rootNode);

        dialogKeep.setVisible(false);
    }

    private boolean notAllParamsOfParamsProvided() {
        for (KeepParameter parameter : editedParameters) {
            String parameterPhrase = parameter.getPhrase();
            for (KeepParameter otherParameters : editedParameters) {
                if (otherParameters == parameter) {
                    // validating that a keep parameter doesn't reference itself
                    if (paramReferencesItself(parameter, parameterPhrase)) return true;
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
                return true;
            }
        }
        return false;
    }

    private static boolean paramReferencesItself(KeepParameter parameter, String parameterPhrase) {
        if (parameterPhrase.contains(String.format("<%s>", parameter.getName()))) {
            JOptionPane.showMessageDialog(Application.getContext().getGui(),
                    String.format("The parameter %s cannot use its own name as a command parameter.", parameter.getName()));
            return true;
        }
        return false;
    }

    private boolean notAllParamsOfCommandProvided() {
        String commandStr = dialogKeep.textKeep.getText();
        for (KeepParameter parameter : editedParameters) {
            commandStr = commandStr.replace(String.format("<%s>", parameter.getName()), "");
        }
        if (commandStr.contains("<") && commandStr.contains(">")) {
            JOptionPane.showMessageDialog(Application.getContext().getGui(),
                    "Given parameters does not cover all keep parameters, please add missing ones");
            return true;
        }
        return false;
    }

    private boolean duplicationsInParamsNames() {
        if (editedParameters.stream().map(KeepParameter::getName).distinct().count() < editedParameters.size()) {
            JOptionPane.showMessageDialog(Application.getContext().getGui(),
                    "There are duplications in parameters names, please review");
            return true;
        }
        return false;
    }

    private boolean mandatoryFieldsNotProvided() {
        if (StringUtils.isEmpty(dialogKeep.textKeep.getText()) || StringUtils.isEmpty(dialogKeep.textName.getText())) {
            JOptionPane.showMessageDialog(Application.getContext().getGui(),
                    "Both Name and Keep fields are mandatory", "Not enough...", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    private void stopEditing() {
        JTable tableParams = dialogKeep.tableParams;
        if (tableParams.isEditing()) {
            TableCellEditor cellEditor = tableParams.getCellEditor(
                    tableParams.getEditingRow(), tableParams.getEditingColumn());
            if (cellEditor != null) {
                cellEditor.stopCellEditing();
            }
        }
    }
}

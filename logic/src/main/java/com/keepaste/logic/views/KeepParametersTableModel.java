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

import com.keepaste.logic.models.KeepParameter;
import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * This class is a model for a Keep's parameters as shown on the {@link com.keepaste.gui.DialogKeep}.
 */
public class KeepParametersTableModel extends AbstractTableModel {
    final String[] columns = {"Parameter name", "Parameter value", "Global"};
    final transient List<KeepParameter> editedParameters;

    public KeepParametersTableModel(List<KeepParameter> editedParameters) {
        this.editedParameters = editedParameters;
    }

    @Override
    public int getRowCount() {
        if (editedParameters != null) {
            return editedParameters.size();
        } else {
            return 0;
        }
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                // parameter name column
                return editedParameters.get(rowIndex).getName();
            case 1:
                // parameter command column
                return editedParameters.get(rowIndex).getPhrase();
            case 2:
                // isGlobal column
                return editedParameters.get(rowIndex).isGlobal();
            default:
                return null;
        }
    }


    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a checkbox.
     */
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

            /*
             * Don't need to implement this method unless your table's
             * editable.
             */
            @Override
            public boolean isCellEditable(int row, int col) {
                //Note that the data/cell address is constant,
                //no matter where the cell appears onscreen.
//                if (col < 2) {
//                    return false;
//                } else {
                    return true;
//                }
            }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        switch (col) {
            case 0:
                // parameter name column
                editedParameters.get(row).setName(value.toString());
                break;
            case 1:
                // parameter command column
                editedParameters.get(row).setPhrase(value.toString());
                break;
            case 2:
                // isGlobal column
                editedParameters.get(row).setGlobal((boolean) value);
                break;
            default:
                // do nothing
        }

        fireTableCellUpdated(row, col);
    }

    public void removeRow(int rowIndex) {
        editedParameters.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void addRow(int rowIndex, String... values) {
        editedParameters.add(new KeepParameter(values[0], values[1]));
        fireTableRowsInserted(rowIndex, rowIndex);
    }
}

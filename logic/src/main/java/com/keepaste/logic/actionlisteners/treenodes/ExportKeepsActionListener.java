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

package com.keepaste.logic.actionlisteners.treenodes;

import com.keepaste.logic.Application;
import com.keepaste.logic.models.KeepNode;
import com.keepaste.logic.views.ViewTree;
import lombok.extern.log4j.Log4j2;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * This class is an ActionListener for exporting a keep tree node.
 */
@Log4j2
public class ExportKeepsActionListener extends BaseTreeNodeActionListener {

    public ExportKeepsActionListener(ViewTree viewTree) {
        super(viewTree);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            log.debug("TreeNodes - Exporting a keep node");
            KeepNode keep = (KeepNode) getViewTree().getSelectedNode().getUserObject();
            String json = Application.getContext().getKeepsManager().getKeepJson(keep);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");
            fileChooser.setSelectedFile(new File(keep.getTitle().concat(".json")));

            int userSelection = fileChooser.showSaveDialog(Application.getContext().getGui());

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave.getPath()))) {
                    writer.write(json);
                }
                log.debug("Save as file: " + fileToSave.getAbsolutePath());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(Application.getContext().getGui(), "Failed to export keeps", "Export keeps", JOptionPane.ERROR_MESSAGE);
            log.error("Failed to export keeps", ex);
        }
    }
}

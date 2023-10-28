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
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This class is an ActionListener for importing a keep tree node.
 */
@Log4j2
public class ImportKeepsActionListener extends BaseTreeNodeActionListener {

    /**
     * Constructor.
     *
     * @param viewTree the Keep's {@link ViewTree}
     */
    public ImportKeepsActionListener(@NonNull final ViewTree viewTree) {
        super(viewTree);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // Create a file chooser dialog
            JFileChooser fileChooser = new JFileChooser();
            // Set the file filter to accept only JSON files
//            FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
//            fileChooser.setFileFilter(filter);
            // Show the file chooser dialog
            int result = fileChooser.showOpenDialog(Application.getContext().getGui());

            // If the user selects a file and clicks "Open"
            if (result == JFileChooser.APPROVE_OPTION) {
                // Get the selected file
                File file = fileChooser.getSelectedFile();
                KeepNode newKeep = Application.getContext().getKeepsManager().getKeepFromJsonFile(file);
                getViewTree().addKeepToTree(newKeep);
                log.debug("TreeNodes - Keep imported to the tree, keep = [{}]", newKeep);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(Application.getContext().getGui(),
                    "Failed to import keeps", "Import keeps", JOptionPane.ERROR_MESSAGE);
            log.error("Failed to import keeps", ex);
        }
    }
}

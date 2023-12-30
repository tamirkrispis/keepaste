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

package com.keepaste.logic.actionlisteners.treenodes;

import com.keepaste.logic.Application;
import com.keepaste.logic.models.KeepsGroup;
import com.keepaste.logic.views.ViewTree;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;

/**
 * This class is an ActionListener for renaming a KeepsGroup tree node.
 */
@Log4j2
public class RenameGroupTreeNodeActionListener extends BaseTreeNodeActionListener {

    public RenameGroupTreeNodeActionListener(@NonNull final ViewTree viewTree) {
        super(viewTree);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("TreeNodes - Renaming group tree node");
        DefaultMutableTreeNode selectedNode = getViewTree().getSelectedNode();
        if (getViewTree().isSelectedNodeGroupNode()) {
            KeepsGroup groupNode = (KeepsGroup) selectedNode.getUserObject();
            String oldTitle = groupNode.getTitle();
            String newGroupTitle = (String) JOptionPane.showInputDialog(
                    Application.getContext().getGui().getContentPane(),
                    "Rename group node",
                    "Group node",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    groupNode.getTitle());
            if (newGroupTitle != null && !newGroupTitle.isEmpty()) {
                groupNode.setTitle(newGroupTitle);
                selectedNode.setUserObject(groupNode);
                getViewTree().resetSelectedNode();
                Application.getContext().getKeepsManager().saveKeeps((KeepsGroup) getViewTree().getRootTreeNode().getUserObject());
                log.debug("TreeNodes - Renamed group tree node from [{}] to [{}]", oldTitle, newGroupTitle);
            } else {
                log.debug("TreeNodes - Renaming was probably cancelled as the new name was not set");
            }
        }
    }
}
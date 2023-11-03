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

/**
 * This class is an ActionListener for deleting a Keep or KeepsGroup tree node.
 */
@Log4j2
public class DeleteTreeNodeActionListener extends BaseTreeNodeActionListener {

    /**
     * Constructor.
     *
     * @param viewTree  {@link ViewTree}
     */
    public DeleteTreeNodeActionListener(@NonNull final ViewTree viewTree) {
        super(viewTree);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("TreeNodes - Deleting a node from the tree");
        if (getViewTree().getRootTreeNode().equals(getViewTree().getSelectedNode())) {
            JOptionPane.showMessageDialog(Application.getContext().getGui(),
                    "You cannot delete the Keeps root node", "Cannot delete root", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int deletionConfirmed = JOptionPane.showConfirmDialog(
                Application.getContext().getGui().getContentPane(),
                String.format("Are you sure you want to delete node \"%s\"",
                        ((KeepNode) getViewTree().getSelectedNode().getUserObject()).getTitle()),
                        "Node deletion", JOptionPane.YES_NO_OPTION);

        if (deletionConfirmed == JOptionPane.YES_OPTION) {
            getViewTree().removeSelectedKeepFromTree();
        } else {
            log.debug("TreeNodes - Deleting a node cancelled by the user");
        }
    }
}

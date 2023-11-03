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

package com.keepaste.logic.managers.tree;

import com.keepaste.logic.Application;
import com.keepaste.logic.models.Keep;
import com.keepaste.logic.models.KeepNode;
import com.keepaste.logic.views.ViewTree;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.dnd.*;

/**
 * This class is a handler for drag/drops over the Keeps tree.
 */
public class DefaultTreeTransferHandler extends BaseTreeTransferHandler {

    private final transient ViewTree viewTree;

    public DefaultTreeTransferHandler(final ViewTree viewTree, final int action) {
        super(viewTree.getTree(), action, true);
        this.viewTree = viewTree;
    }

    public boolean canPerformAction(JTree target, DefaultMutableTreeNode draggedNode, int action, Point location) {
        TreePath pathTarget = target.getPathForLocation(location.x, location.y);
        if (pathTarget == null) {
            target.setSelectionPath(null);
            return false;
        }
        target.setSelectionPath(pathTarget);
        DefaultMutableTreeNode nodeToDropOn = (DefaultMutableTreeNode) pathTarget.getLastPathComponent();
        return !draggedNode.isRoot() && draggedNode != nodeToDropOn && draggedNode.getParent() != nodeToDropOn && !(nodeToDropOn.getUserObject() instanceof Keep);
    }

    @Override
    public boolean executeDrop(JTree tree, DefaultMutableTreeNode draggedNode, DefaultMutableTreeNode newParentNode, int action) {
//        if (action == DnDConstants.ACTION_COPY) {
//            viewTree.addKeepToTree((KeepNode) draggedNode.getUserObject(), newParentNode);
//        }
        if (action == DnDConstants.ACTION_MOVE) {
            if (newParentNode != null) {
                KeepNode draggedKeepNode = (KeepNode) draggedNode.getUserObject();
                KeepNode targetKeepNode = (KeepNode) newParentNode.getUserObject();
                int result = JOptionPane.showConfirmDialog(Application.getContext().getGui(), String.format("Are you sure you want to move \"%s\" to be under \"%s\"?", draggedKeepNode.getTitle(), targetKeepNode.getTitle()), "Move node confirmation", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    viewTree.removeKeepFromTree(draggedNode);
                    viewTree.addKeepToTree((KeepNode) draggedNode.getUserObject(), newParentNode);
                }
            }
            return true;
        }
        return false;
    }
}

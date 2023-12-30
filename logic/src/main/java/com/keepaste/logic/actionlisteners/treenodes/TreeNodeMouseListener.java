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
import com.keepaste.logic.models.Keep;
import com.keepaste.logic.models.KeepNode;
import com.keepaste.logic.models.KeepsGroup;
import lombok.Getter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TreeNodeMouseListener extends MouseAdapter {

    @Getter
    private DefaultMutableTreeNode selectedNode;
    @Getter
    private DefaultMutableTreeNode parentOfSelectedNode;
    private final JPopupMenu treeKeepNodeContextMenu;
    private final JPopupMenu treeGroupNodeContextMenu;

    public TreeNodeMouseListener(JPopupMenu treeGroupNodeContextMenu,
                                 JPopupMenu treeKeepNodeContextMenu) {
        this.treeKeepNodeContextMenu = treeKeepNodeContextMenu;
        this.treeGroupNodeContextMenu = treeGroupNodeContextMenu;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        treeGroupNodeContextMenu.setVisible(false);
        JTree tree = Application.getContext().getGui().tree;
        TreePath path =  tree.getPathForLocation(e.getX(), e.getY());
        tree.setSelectionPath(path);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();

        if (node == null) {
            //Nothing is selected.
            return;
        }

        if (SwingUtilities.isRightMouseButton(e) || e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {
            KeepNode keepNode = (KeepNode) node.getUserObject();
            if (node.isLeaf() && !(keepNode instanceof KeepsGroup)) {
                // Keep node
                selectedNode = node;
                parentOfSelectedNode = (DefaultMutableTreeNode) node.getParent();
                EventQueue.invokeLater(() -> treeKeepNodeContextMenu.show(e.getComponent(), e.getX(), e.getY()));
            } else {
                // Keep group node
                selectedNode = node;
                parentOfSelectedNode = (DefaultMutableTreeNode) node.getParent();
                EventQueue.invokeLater(() -> treeGroupNodeContextMenu.show(e.getComponent(), e.getX(), e.getY()));
            }
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            tree.setSelectionPath(tree.getPathForLocation(e.getX(), e.getY()));

            DefaultMutableTreeNode clickedOnNode = (DefaultMutableTreeNode)
                    tree.getLastSelectedPathComponent();

            if (clickedOnNode == null) {
                //Nothing is selected.
                return;
            }

            KeepNode keepNode = (KeepNode) clickedOnNode.getUserObject();
            if (e.getClickCount() == 2 && clickedOnNode.isLeaf() && !(keepNode instanceof KeepsGroup)) {
                Application.getContext().getKeepExecutionManager().executeKeepOnWindow((Keep) clickedOnNode.getUserObject());
            }
        }
    }
}

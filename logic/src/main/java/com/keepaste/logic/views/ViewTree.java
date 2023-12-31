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

import com.keepaste.logic.Application;
import com.keepaste.logic.actionlisteners.treenodes.*;
import com.keepaste.logic.managers.tree.CustomTreeCellRenderer;
import com.keepaste.logic.managers.tree.DefaultTreeTransferHandler;
import com.keepaste.logic.managers.tree.KeepsTreeModelListener;
import com.keepaste.logic.models.*;
import com.keepaste.logic.utils.ImagesUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.event.*;
import java.util.Enumeration;

/**
 * This View class manages the Keeps tree.
 */
@Log4j2
public class ViewTree implements View {
    public static final int TOOLTIP_SHOW_DELAY_IN_MS = 500;
    public static final int TOOLTIP_DISMISS_DELAY_IN_MS = 30000;
    @Getter
    private final JTree tree;
    @Getter
    private DefaultMutableTreeNode rootTreeNode;
    private TreeNodeMouseListener treeNodeMouseListener;
    private final DeleteTreeNodeActionListener deleteTreeNodeActionListener;
    private final ImportKeepsActionListener importKeepsActionListener;
    private final ExportKeepsActionListener exportKeepsActionListener;

    public ViewTree() {
        deleteTreeNodeActionListener = new DeleteTreeNodeActionListener(this);
        importKeepsActionListener = new ImportKeepsActionListener(this);
        exportKeepsActionListener = new ExportKeepsActionListener(this);
        tree = Application.getContext().getGui().tree;
    }

    @Override
    public void updateObserver(Model model) {
        EventQueue.invokeLater(() -> {
            initKeepsTree();
            JPopupMenu treeKeepNodeContextMenu = initKeepNodeContextMenu();
            JPopupMenu treeGroupNodeContextMenu = initKeepGroupNodeContextMenu();

            addListenersToKeepsTree(treeKeepNodeContextMenu, treeGroupNodeContextMenu);
        });
    }

    public DefaultMutableTreeNode getSelectedNode() {
        return treeNodeMouseListener != null ? treeNodeMouseListener.getSelectedNode() : null;
    }

    public DefaultMutableTreeNode getParentOfSelectedNode() {
        return treeNodeMouseListener != null ? treeNodeMouseListener.getParentOfSelectedNode() : null;
    }

    private JPopupMenu initKeepNodeContextMenu() {
        JPopupMenu treeKeepNodeContextMenu = new JPopupMenu("Keep configuration");
        JMenuItem refreshParamsMenuItem = new JMenuItem("Execute with refreshed parameters");
        refreshParamsMenuItem.addActionListener(new RunKeepWithRefreshedParametersActionListener(this));
        JMenuItem editMenuItem = new JMenuItem("Edit...");
        editMenuItem.addActionListener(new EditMenuItemActionListener(this));
        JMenuItem exportKeepMenuItem = new JMenuItem("Export Keep...");
        exportKeepMenuItem.addActionListener(exportKeepsActionListener);
        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.addActionListener(deleteTreeNodeActionListener);
        treeKeepNodeContextMenu.add(refreshParamsMenuItem);
        treeKeepNodeContextMenu.add(new JSeparator());
        treeKeepNodeContextMenu.add(editMenuItem);
        treeKeepNodeContextMenu.add(new JSeparator());
        treeKeepNodeContextMenu.add(exportKeepMenuItem);
        treeKeepNodeContextMenu.add(new JSeparator());
        treeKeepNodeContextMenu.add(deleteMenuItem);
        return treeKeepNodeContextMenu;
    }


    private JPopupMenu initKeepGroupNodeContextMenu() {
        JPopupMenu treeGroupNodeContextMenu = new JPopupMenu("Group node configuration");
        JMenuItem renameGroupMenuItem = new JMenuItem("Rename...");
        renameGroupMenuItem.addActionListener(new RenameGroupTreeNodeActionListener(this));
        JMenuItem addGroupMenuItem = new JMenuItem("Add group...");
        addGroupMenuItem.addActionListener(new AddGroupTreeNodeActionListener(this));
        JMenuItem addKeepInGroupMenuItem = new JMenuItem("Add Keep...");
        addKeepInGroupMenuItem.addActionListener(new AddKeepTreeNodeActionListener(this));
        JMenuItem deleteGroupMenuItem = new JMenuItem("Delete Group");
        deleteGroupMenuItem.addActionListener(deleteTreeNodeActionListener);
        JMenuItem importKeepsMenuItem = new JMenuItem("Import Keeps...");
        importKeepsMenuItem.addActionListener(importKeepsActionListener);
        JMenuItem exportKeepsMenuItem = new JMenuItem("Export Keeps...");
        exportKeepsMenuItem.addActionListener(exportKeepsActionListener);
        treeGroupNodeContextMenu.add(renameGroupMenuItem);
        treeGroupNodeContextMenu.add(addGroupMenuItem);
        treeGroupNodeContextMenu.add(addKeepInGroupMenuItem);
        treeGroupNodeContextMenu.add(new JSeparator());
        treeGroupNodeContextMenu.add(importKeepsMenuItem);
        treeGroupNodeContextMenu.add(exportKeepsMenuItem);
        treeGroupNodeContextMenu.add(new JSeparator());
        treeGroupNodeContextMenu.add(deleteGroupMenuItem);
        return treeGroupNodeContextMenu;
    }

    public boolean isSelectedNodeGroupNode() {
        return getSelectedNode() != null && getSelectedNode().getUserObject() instanceof KeepsGroup;
    }

    private void initKeepsTree() {
        KeepsGroup rootKeepNode = Application.getContext().getKeepsManager().getRootNode();
        rootTreeNode = new DefaultMutableTreeNode(rootKeepNode);

        TreeModel treeModel = new DefaultTreeModel(rootTreeNode);
        treeModel.addTreeModelListener(new KeepsTreeModelListener());

        tree.setRootVisible(true);
        tree.setModel(treeModel);

        for (KeepNode childNode : rootKeepNode.getKeepsNodes()) {
            addKeepToTree(childNode, rootTreeNode, false);
        }

        ToolTipManager.sharedInstance().registerComponent(tree);
        ToolTipManager.sharedInstance().setInitialDelay(TOOLTIP_SHOW_DELAY_IN_MS);
        ToolTipManager.sharedInstance().setReshowDelay(TOOLTIP_SHOW_DELAY_IN_MS);
        ToolTipManager.sharedInstance().setDismissDelay(TOOLTIP_DISMISS_DELAY_IN_MS);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(true);

        setCustomCellRenderer();
        tree.setShowsRootHandles(true);
        tree.setDragEnabled(true);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setTransferHandler(new DefaultTreeTransferHandler(this, DnDConstants.ACTION_MOVE));
        tree.repaint();
        tree.expandRow(0);
    }

    protected void setCustomCellRenderer() {
        CustomTreeCellRenderer renderer = new CustomTreeCellRenderer();
        tree.setCellRenderer(renderer);
        Icon closedIcon;
        Icon openedIcon;
        Icon leafIcon;

        closedIcon = ImagesUtils.getImageIconFromFilePath("/folder.png");
        openedIcon = ImagesUtils.getImageIconFromFilePath("/folder.png");
        leafIcon = ImagesUtils.getDefaultKeepNodeIcon();

        renderer.setClosedIcon(closedIcon);
        renderer.setOpenIcon(openedIcon);
        renderer.setLeafIcon(leafIcon);

        resetAllNodes();
    }

    public void resetSelectedNode() {
        DefaultTreeModel model = (DefaultTreeModel)getTree().getModel();
        model.nodeChanged(getSelectedNode());
    }

    public void addKeepToTree(KeepNode keepNode) {
        addKeepToTree(keepNode, true, getSelectedNode(), true);
    }

    public void addKeepToTree(KeepNode keepNode, DefaultMutableTreeNode parentTreeNode) {
        addKeepToTree(keepNode, true, parentTreeNode, true);
    }

    public void addKeepToTree(KeepNode keepNode, DefaultMutableTreeNode parentTreeNode, boolean save) {
        addKeepToTree(keepNode, true, parentTreeNode, save);
    }

    private void addKeepToTree(KeepNode newKeep, boolean initialKeep, DefaultMutableTreeNode parentTreeNode, boolean save) {
        DefaultTreeModel treeModel = (DefaultTreeModel) getTree().getModel();
        DefaultMutableTreeNode newKeepTreeNode = new DefaultMutableTreeNode(newKeep);
        treeModel.insertNodeInto(newKeepTreeNode, parentTreeNode, getInsertIndex(newKeep, parentTreeNode));
        if (save && initialKeep) { // as the newKeep already holds all the child Keeps, we add it only once when the initialKeep is true
            ((KeepsGroup) parentTreeNode.getUserObject()).getKeepsNodes().add(newKeep);
        }

        if (newKeep instanceof KeepsGroup) {
            for (KeepNode childNode : ((KeepsGroup) newKeep).getKeepsNodes()) {
                addKeepToTree(childNode, false, newKeepTreeNode, save);
            }
        }

        // this is to identify when we finished the recursion, then save what was done
        if (initialKeep) {
            Application.getContext().getKeepsManager().saveKeeps((KeepsGroup) getRootTreeNode().getUserObject());
            log.debug("TreeNodes - Added Keep to the tree, Keep = [{}]", newKeep);
        }
    }

    public void removeKeepFromTree(DefaultMutableTreeNode treeNodeToRemove) {
        KeepNode keepNodeToRemove = (KeepNode)treeNodeToRemove.getUserObject();
        ((KeepsGroup)((DefaultMutableTreeNode)treeNodeToRemove.getParent()).getUserObject()).getKeepsNodes().remove(keepNodeToRemove);
        Application.getContext().getKeepsManager().saveKeeps((KeepsGroup)getRootTreeNode().getUserObject());
        DefaultTreeModel treeModel = (DefaultTreeModel)getTree().getModel();
        treeModel.removeNodeFromParent(treeNodeToRemove);
        log.debug("TreeNodes - Deleted a node from the tree, deleted node [{}]", treeNodeToRemove);
    }

    public void removeSelectedKeepFromTree() {
        DefaultTreeModel treeModel = (DefaultTreeModel)getTree().getModel();
        treeModel.removeNodeFromParent(getSelectedNode());
        KeepNode nodeToRemove = (KeepNode)getSelectedNode().getUserObject();
        ((KeepsGroup)getParentOfSelectedNode().getUserObject()).getKeepsNodes().remove(nodeToRemove);
        Application.getContext().getKeepsManager().saveKeeps((KeepsGroup)getRootTreeNode().getUserObject());
        log.debug("TreeNodes - Deleted a node from the tree, deleted node [{}]", nodeToRemove);
    }


    private void resetAllNodes() {
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) (tree.getModel()).getRoot();
        if (rootNode != null) {
            Enumeration<TreeNode> nodes = rootNode.depthFirstEnumeration();
            TreeNode current;
            while (nodes.hasMoreElements()) {
                current = nodes.nextElement();
                model.nodeChanged(current);
            }
        }
    }

    private int getInsertIndex(KeepNode childKeep, DefaultMutableTreeNode parentTreeNode) {
        int i;
        for (i = 0; i < parentTreeNode.getChildCount(); i++) {
            DefaultMutableTreeNode childTreeNode = (DefaultMutableTreeNode) parentTreeNode.getChildAt(i);
            KeepNode childKeepNode = (KeepNode) childTreeNode.getUserObject();
            // if this is the first group, add it in index 0
            if (childKeep instanceof KeepsGroup && childKeepNode instanceof Keep) {
                return i;
            }
            if (((childKeep instanceof KeepsGroup && childKeepNode instanceof KeepsGroup)
                    || (childKeep instanceof Keep && childKeepNode instanceof Keep)) &&
                    StringUtils.compareIgnoreCase(childKeepNode.getTitle(), childKeep.getTitle()) > 0) {
                return i;
            }
        }
        return i;
    }

    private void addListenersToKeepsTree(JPopupMenu treeKeepNodeContextMenu, JPopupMenu treeGroupNodeContextMenu) {
        treeNodeMouseListener = new TreeNodeMouseListener(
                treeGroupNodeContextMenu,
                treeKeepNodeContextMenu);
        tree.addMouseListener(treeNodeMouseListener);

        KeyListener listener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    DefaultMutableTreeNode clickedOnNode = (DefaultMutableTreeNode)
                            tree.getLastSelectedPathComponent();

                    if (clickedOnNode == null) {
                        //Nothing is selected.
                        return;
                    }

                    KeepNode keepNode = (KeepNode) clickedOnNode.getUserObject();

                    if (clickedOnNode.isLeaf() && !(keepNode instanceof KeepsGroup)) {
                        Application.getContext().getKeepExecutionManager().executeKeepOnWindow((Keep) clickedOnNode.getUserObject());
                    }
                }
            }
        };
        tree.addKeyListener(listener);
    }
}

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

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * This is a base class for handling common and generic drag and drops over a tree.
 */
@Log4j2
public abstract class BaseTreeTransferHandler
    extends TransferHandler
        implements DragGestureListener,
        DragSourceListener,
        DropTargetListener {

    private final JTree tree;
    private final DragSource dragSource; // dragsource
    private final DropTarget dropTarget; //droptarget
    private static DefaultMutableTreeNode draggedNode;
    private DefaultMutableTreeNode draggedNodeParent;
    private JLabel draggedLabel;
    private JLayeredPane dragPane;
    private boolean drawImage;

    protected BaseTreeTransferHandler(@NonNull final JTree tree, final int action, final boolean drawIcon) {
        this.tree = tree;
        drawImage = drawIcon;
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(tree, action, this);
        dropTarget = new DropTarget(tree, action, this);
    }

    /* Methods for DragSourceListener */
    public void dragDropEnd(DragSourceDropEvent dsde) {
        if (drawImage) {
            dragPane.remove(draggedLabel);
            dragPane.repaint(draggedLabel.getBounds());
        }
        if (dsde.getDropSuccess() && dsde.getDropAction() == DnDConstants.ACTION_MOVE && draggedNodeParent != null) {
            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(draggedNodeParent);
            tree.expandPath(new TreePath(draggedNodeParent.getPath()));
            tree.expandPath(new TreePath(draggedNode.getPath()));
        }
    }

    public final void dragEnter(DragSourceDragEvent dsde) {
        int action = dsde.getDropAction();
        if (action == DnDConstants.ACTION_COPY) {
            dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
        } else {
            if (action == DnDConstants.ACTION_MOVE) {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
            } else {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
            }
        }
    }

    public final void dragOver(DragSourceDragEvent dsde) {
        int action = dsde.getDropAction();
        if (action == DnDConstants.ACTION_COPY) {
            dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
        } else {
            if (action == DnDConstants.ACTION_MOVE) {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
            } else {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
            }
        }
    }

    public final void dropActionChanged(DragSourceDragEvent dsde) {
        int action = dsde.getDropAction();
        if (action == DnDConstants.ACTION_COPY) {
            dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
        } else {
            if (action == DnDConstants.ACTION_MOVE) {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
            } else {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
            }
        }
    }

    public final void dragExit(DragSourceEvent dse) {
        dse.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
    }

    /* Methods for DragGestureListener */
    public final void dragGestureRecognized(DragGestureEvent dge) {
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            draggedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            List<Boolean> expandedStates = new ArrayList<>();
            for (Enumeration enumeration = draggedNode.depthFirstEnumeration(); enumeration.hasMoreElements();) {
                DefaultMutableTreeNode element = (DefaultMutableTreeNode) enumeration.nextElement();
                TreePath treePath = new TreePath(element.getPath());
                expandedStates.add(tree.isExpanded(treePath));
            }
            draggedNodeParent = (DefaultMutableTreeNode) draggedNode.getParent();
            BufferedImage image = null;
            if (drawImage) {
                Rectangle pathBounds = tree.getPathBounds(path); //getpathbounds of selectionpath
                JComponent lbl = (JComponent)tree.getCellRenderer().getTreeCellRendererComponent(
                        tree, draggedNode, false, tree.isExpanded(path),
                        (tree.getModel()).isLeaf(path.getLastPathComponent()), 0, false); //returning the label
                lbl.setBounds(pathBounds); //setting bounds to lbl
                image = new BufferedImage(
                        lbl.getWidth(), lbl.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE); //buffered image reference passing the label's ht and width
                Graphics2D graphics = image.createGraphics(); //creating the graphics for buffered image
                graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); //Sets the Composite for the Graphics2D context
                lbl.setOpaque(false);
                lbl.paint(graphics);
                graphics.dispose();
                draggedLabel = new JLabel(new ImageIcon(image));
                draggedLabel.setOpaque(false);
                draggedLabel.setBounds(pathBounds);
                Container container = tree.getTopLevelAncestor();
                if (container == null) {
                    drawImage = false;
                } else {
                    if (container instanceof JWindow) {
                        dragPane = ((JWindow) tree.getTopLevelAncestor()).getLayeredPane();
                        dragPane.add(draggedLabel, JLayeredPane.DRAG_LAYER);
                    } else if (container instanceof JFrame) {
                        dragPane = ((JFrame) tree.getTopLevelAncestor()).getLayeredPane();
                        dragPane.add(draggedLabel, JLayeredPane.DRAG_LAYER);
                    } else if (container instanceof JApplet) {
                        dragPane = ((JApplet) tree.getTopLevelAncestor()).getLayeredPane();
                        dragPane.add(draggedLabel, JLayeredPane.DRAG_LAYER);
                    } else {
                        drawImage = false;
                    }
                }
            }
            dragSource.startDrag(dge, DragSource.DefaultMoveNoDrop, image, new Point(0, 0), new TransferableNode(draggedNode, expandedStates), this);
        }
    }

    /* Methods for DropTargetListener */
    public final void dragEnter(DropTargetDragEvent dtde) {
        Point pt = dtde.getLocation();
        int action = dtde.getDropAction();
        if (drawImage) {
            paintImage(pt, ((DropTarget)dtde.getSource()).getComponent());
        }
        if (canPerformAction(tree, draggedNode, action, pt)) {
            dtde.acceptDrag(action);
        } else {
            dtde.rejectDrag();
        }
    }

    public final void dragExit(DropTargetEvent dte) {
    }

    public final void dragOver(DropTargetDragEvent dtde) {
        Point pt = dtde.getLocation();
        int action = dtde.getDropAction();
        //tree.autoscroll(pt);
        if (drawImage) {
            paintImage(pt, ((DropTarget) dtde.getSource()).getComponent());
        }
        if (canPerformAction(tree, draggedNode, action, pt)) {
            dtde.acceptDrag(action);
        } else {
            dtde.rejectDrag();
        }
    }

    public final void dropActionChanged(DropTargetDragEvent dtde) {
        Point pt = dtde.getLocation();
        int action = dtde.getDropAction();
        if (drawImage) {
            paintImage(pt, ((DropTarget) dtde.getSource()).getComponent());
        }
        if (canPerformAction(tree, draggedNode, action, pt)) {
            dtde.acceptDrag(action);
        } else {
            dtde.rejectDrag();
        }
    }

    public final void drop(DropTargetDropEvent dtde) {
        try {
            int action = dtde.getDropAction();
            Transferable transferable = dtde.getTransferable();
            Point pt = dtde.getLocation();
            if (transferable.isDataFlavorSupported(TransferableNode.NODE_FLAVOR) && canPerformAction(tree, draggedNode, action, pt)) {
                TreePath pathTarget = tree.getPathForLocation(pt.x, pt.y);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) transferable.getTransferData(TransferableNode.NODE_FLAVOR);
                DefaultMutableTreeNode newParentNode = (DefaultMutableTreeNode )pathTarget.getLastPathComponent();
                if (executeDrop(tree, node, newParentNode, action)) {
                    dtde.acceptDrop(action);
                    dtde.dropComplete(true);
                    tree.expandPath(new TreePath(newParentNode.getPath()));
                    return;
                }
            }
            dtde.rejectDrop();
            dtde.dropComplete(false);
        } catch (Exception e) {
            log.error("Failed to perform drop on tree", e);
            dtde.rejectDrop();
            dtde.dropComplete(false);
        }
    }

    private synchronized void paintImage(Point pt, Component source) {
        pt = SwingUtilities.convertPoint(source, pt, dragPane);
        draggedLabel.setLocation(pt);
    }

    public abstract boolean canPerformAction(JTree target, DefaultMutableTreeNode draggedNode, int action, Point location);
    public abstract boolean executeDrop(JTree tree, DefaultMutableTreeNode draggedNode, DefaultMutableTreeNode newParentNode, int action);
}

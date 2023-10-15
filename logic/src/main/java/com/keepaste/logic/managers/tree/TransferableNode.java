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

import java.awt.datatransfer.*;
import java.util.*;
import javax.swing.tree.*;

/**
 * This class represented a single node that can be transferred as part of a drag/drop operation of the Keeps tree.
 */
public class TransferableNode implements Transferable {
    public static final DataFlavor NODE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "Node");
    public static final DataFlavor EXPANDED_STATE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "Vector");
    private final DefaultMutableTreeNode node;
    private final List<Boolean> expandedStates;
    private final DataFlavor[] flavors = { NODE_FLAVOR, EXPANDED_STATE_FLAVOR };
    public TransferableNode(final DefaultMutableTreeNode nd, final List<Boolean> es) {
        node = nd;
        expandedStates = es;
    }
    public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor == NODE_FLAVOR) {
            return node;
        } else if (flavor == EXPANDED_STATE_FLAVOR) {
            return expandedStates;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return Arrays.asList(flavors).contains(flavor);
    }
}

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

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class KeepsTreeModelListener implements TreeModelListener {
    /**
     * When tree nodes were changed.
     *
     * @param e a {@code TreeModelEvent} describing changes to a tree model
     */
    public void treeNodesChanged(TreeModelEvent e) {
        // do nothing
    }

    /**
     * When tree nodes were inserted.
     *
     * @param e a {@code TreeModelEvent} describing changes to a tree model
     */
    public void treeNodesInserted(TreeModelEvent e) {
        // do nothing
    }

    /**
     * When tree nodes were removed.
     *
     * @param e a {@code TreeModelEvent} describing changes to a tree model
     */
    public void treeNodesRemoved(TreeModelEvent e) {
        // do nothing
    }

    /**
     * When tree nodes structure changed.
     *
     * @param e a {@code TreeModelEvent} describing changes to a tree model
     */
    public void treeStructureChanged(TreeModelEvent e) {
        // do nothing
    }
}

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
import com.keepaste.logic.models.Keep;
import com.keepaste.logic.views.ViewTree;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;

/**
 * This class is an ActionListener for executing a Keep node with refreshed (Cleared) parameters.
 */
@Log4j2
public class RunKeepWithRefreshedParametersActionListener extends BaseTreeNodeActionListener {

    public RunKeepWithRefreshedParametersActionListener(@NonNull final ViewTree viewTree) {
        super(viewTree);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode selectedNode = getViewTree().getSelectedNode();
        log.debug("TreeNodes - Running a keep with refreshed parameters, selected node [{}]", selectedNode);
        if (selectedNode != null &&  selectedNode.getUserObject() instanceof Keep) {
            Application.getContext().getKeepExecutionManager().executeKeepOnWindow((Keep) selectedNode.getUserObject(), true);
        }
        log.debug("TreeNodes - Ran a keep with refreshed parameters, selected node [{}]", selectedNode);
    }
}

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

import com.keepaste.logic.models.Keep;
import com.keepaste.logic.views.ViewDialogKeep;
import com.keepaste.logic.views.ViewTree;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import java.awt.event.ActionEvent;

/**
 * This class is an ActionListener for adding a new keep tree node.
 */
@Log4j2
public class AddKeepTreeNodeActionListener extends BaseTreeNodeActionListener {

    /**
     * Constructor.
     *
     * @param viewTree the Keeps tree
     */
    public AddKeepTreeNodeActionListener(@NonNull final ViewTree viewTree) {
        super(viewTree);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("TreeNodes - Adding a keep to the tree");
        if (getViewTree().isSelectedNodeGroupNode()) {
            var newKeep = new Keep();
            new ViewDialogKeep(newKeep);

            if (!StringUtils.isEmpty(newKeep.getTitle()) && !StringUtils.isEmpty(newKeep.getPhrase())) {
                getViewTree().addKeepToTree(newKeep);
            } else {
                log.debug("TreeNodes - User cancelled adding the new keep");
            }
        } else {
            log.debug("TreeNodes - User tries adding a new keep under a keep node (and not a group node)");
        }

    }
}

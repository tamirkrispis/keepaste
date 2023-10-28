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

import com.keepaste.logic.models.Keep;
import com.keepaste.logic.models.KeepsGroup;
import com.keepaste.logic.models.KeepNode;
import com.keepaste.logic.utils.ImagesUtils;
import lombok.Getter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.Set;

/**
 * This class is a custom renderer for the tree in order to modify the tree's look and feel.
 */
public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final Set<String> KNOWN_KEEPS_ICONS = Set.of("mvn");

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        KeepNode keepNode = null;
        if (((DefaultMutableTreeNode) value).getUserObject() instanceof KeepNode) {
            keepNode = (KeepNode) ((DefaultMutableTreeNode) value).getUserObject();
        }

        Icon icon;
        if (leaf && !(keepNode instanceof KeepsGroup)) {
            Keep keep = (Keep) keepNode;
            icon = getIconForKeep(keep);
            if (keepNode != null) {
                setToolTipText(((Keep) keepNode).toStringHTML());
            }
        } else if (expanded) {
            icon = getOpenIcon();
            setToolTipText("");
        } else {
            icon = getClosedIcon();
            setToolTipText("");
        }

        label.setIcon(icon);

        return label;
    }

    private Icon getIconForKeep(Keep keep) {
        Icon icon = getLeafIcon();
        if (keep == null) {
            icon = getClosedIcon();
        } else {
            if (keep.getPhrase() != null && !keep.getPhrase().isEmpty()) {
                String[] coreCommandExecutable = keep.getPhrase().trim().split(" ");
                String coreCommand = coreCommandExecutable[0];

                if (KNOWN_KEEPS_ICONS.contains(coreCommand)) {
                    // take approved icon form 3rd party
                    icon = ImagesUtils.getImageIconFromFilePath("/Commands/".concat(coreCommand).concat(".png"));
                } else {
                    // generate custom icon if needed
                    icon = ImagesUtils.getImageIconAndGenerateIfNotPresent(coreCommandExecutable[0]/*, (ImageIcon)getLeafIcon()*/);
                }
            }
        }
        return icon;
    }


    enum IconType {
        MAVEN("mvn");

        @Getter
        private final String value;

        IconType(final String value) {
            this.value = value;
        }
    }
}

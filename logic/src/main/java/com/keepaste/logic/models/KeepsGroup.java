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

package com.keepaste.logic.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a group of Keeps.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("KeepsGroup")
public class KeepsGroup extends KeepNode {
    private List<KeepNode> keepsNodes = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param title         the title of the group
     */
    public KeepsGroup(final String title) {
        super(title, null);
    }

    /**
     * Constructor.
     *
     * @param title         the title of the group
     * @param icon          the icon of the group
     */
    public KeepsGroup(final String title, final Icon icon) {
        super(title, icon);
    }

    /**
     * Constructor.
     *
     * @param title         the title of the group
     * @param icon          the icon of the group
     * @param keepsNodes    the child keeps nodes
     */
    public KeepsGroup(final String title, final Icon icon, final List<KeepNode> keepsNodes) {
        super(title, icon);
        this.keepsNodes = keepsNodes;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}

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

package com.keepaste.logic.controllers;

import com.keepaste.logic.Application;
import com.keepaste.logic.models.KeepsGroup;
import com.keepaste.logic.models.Model;
import com.keepaste.logic.models.ModelTree;
import com.keepaste.logic.views.ViewTree;
import lombok.NonNull;
import java.io.IOException;

/**
 * This class is a {@link BaseController} observes for changes in the {@link ModelTree} and notifies the {@link ViewTree}.
 */
public class ControllerTree extends BaseController {

    private final ModelTree modelTree;

    /**
     * Constructor.
     *
     * @param modelTree     {@link ModelTree}
     * @param viewTree      {@link ViewTree}
     * @throws IOException  on error
     */
    public ControllerTree(@NonNull final ModelTree modelTree,
                          @NonNull final ViewTree viewTree) throws IOException {
        this.modelTree = modelTree;

        // listen on model changes
        modelTree.registerObserver(this);

        // have the view listen on this controller's changes
        this.registerObserver(viewTree);

        // loading the tree for the first time
        initTree();
    }

    @Override
    public void updateObserver(Model model) {
        // something changed on the model, performing logic and updating the view

    }

    private void initTree() throws IOException {
        KeepsGroup rootKeepNode = Application.getContext().getKeepsManager().loadKeeps();
        Application.getContext().getKeepsManager().sort();
        modelTree.setRootKeepNode(rootKeepNode);
        updateAllObservers(modelTree);
    }
}

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

package com.keepaste.logic.controllers;

import com.keepaste.logic.Application;
import com.keepaste.logic.common.BaseSubject;
import com.keepaste.logic.common.Observer;
import com.keepaste.logic.managers.SettingsManager;
import com.keepaste.logic.models.Model;
import com.keepaste.logic.models.ModelSettings;
import com.keepaste.logic.views.ViewLookAndFeel;
import lombok.NonNull;

/**
 * This class is the {@code BaseController} for look and feel changes from the {@code ModelSettings}.
 */
public class ControllerLookAndFeel extends BaseSubject implements Observer {


    /**
     * Constructor.
     *
     * @param modelSettings     {@code ModelSettings}
     * @param viewLookAndFeel   {@code ViewLookAndFeel}
     */
    public ControllerLookAndFeel(@NonNull final ModelSettings modelSettings,
                                 @NonNull final ViewLookAndFeel viewLookAndFeel) {
        // ModelSetting -> ControllerLookAndFeel -> ViewLookAndFeel
        // listening on the model
        modelSettings.registerObserver(this);
        // view listens on this
        this.registerObserver(viewLookAndFeel);
    }

    @Override
    public void updateObserver(Model model) {
        // saving the change
        SettingsManager.saveSettingsToFile(Application.getContext().getModelSettings());
        // update the view, if any
        this.updateAllObservers(model);
    }
}

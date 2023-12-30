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
import com.keepaste.logic.models.Model;
import com.keepaste.logic.models.ModelSettings;
import com.keepaste.logic.utils.ImagesUtils;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.*;

/**
 * This View class manages the look and feel of the app (themes).
 */
@Log4j2
public class ViewLookAndFeel implements View {

    private final ViewTree viewTree;

    /**
     * Constructor.
     *
     * @param viewTree the {@code ViewTree}
     */
    public ViewLookAndFeel(@NonNull final ViewTree viewTree) {
        this.viewTree = viewTree;
    }

    @Override
    public void updateObserver(Model model) {
        EventQueue.invokeLater(() -> {
            try {
                ModelSettings modelSettings = (ModelSettings) model;
                UIManager.setLookAndFeel(modelSettings.getTheme());
                SwingUtilities.updateComponentTreeUI(Application.getContext().getGui());

                viewTree.setCustomCellRenderer();

                Application.getContext().getGui().setAlwaysOnTop(modelSettings.isAlwaysOnTop());
                switch (Application.getContext().getModelSettings().getTheme().getName()) {
                    case "FlatLaf macOS Dark":
                        Application.getContext().getGui().menuItemMain.setIcon(ImagesUtils.getImageIconFromFilePath("/Dark/menu-dark.png"));
                        Application.getContext().getGui().menuItemAbout.setIcon(ImagesUtils.getImageIconFromFilePath("/Dark/info-dark.png"));
                        Application.getContext().getGui().menuItemHeart.setIcon(ImagesUtils.getImageIconFromFilePath("/Dark/heart-dark.png"));
                        Application.getContext().getGui().labelTargetWindow.setIcon(ImagesUtils.getImageIconFromFilePath("/Dark/target-window-dark.png"));
                        break;
                    case "FlatLaf macOS Light":
                    default:
                        Application.getContext().getGui().menuItemMain.setIcon(ImagesUtils.getImageIconFromFilePath("/Light/menu.png"));
                        Application.getContext().getGui().menuItemAbout.setIcon(ImagesUtils.getImageIconFromFilePath("/Light/info.png"));
                        Application.getContext().getGui().menuItemHeart.setIcon(ImagesUtils.getImageIconFromFilePath("/Light/heart.png"));
                        Application.getContext().getGui().labelTargetWindow.setIcon(ImagesUtils.getImageIconFromFilePath("/Light/target-window.png"));

                        break;
                }
            } catch (UnsupportedLookAndFeelException e) {
                log.error("Unsupported look and feel was set", e);
            }
        });
    }

}

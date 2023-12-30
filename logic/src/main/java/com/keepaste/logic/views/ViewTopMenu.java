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

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.keepaste.logic.Application;
import com.keepaste.logic.actionlisteners.topmenu.AlwaysOnTopActionListener;
import com.keepaste.logic.actionlisteners.topmenu.DialogAboutActionListener;
import com.keepaste.logic.actionlisteners.topmenu.DialogHeartActionListener;
import com.keepaste.logic.actionlisteners.topmenu.ExitActionListener;
import com.keepaste.logic.actionlisteners.topmenu.AutoEnterActionListener;
import com.keepaste.logic.actionlisteners.topmenu.FocusOnTargetWindowActionListener;
import com.keepaste.logic.actionlisteners.topmenu.CopyToClipboardActionListener;
import com.keepaste.logic.actionlisteners.topmenu.PathMenuItemActionListener;
import com.keepaste.logic.actionlisteners.topmenu.ThemesMenuItemActionListener;
import com.keepaste.logic.utils.OperatingSystemUtils;
import lombok.Getter;
import lombok.NonNull;

import javax.swing.*;

/**
 * This View class manages the menu items.
 */
public class ViewTopMenu {

    private final JMenu menuItemMain;
    private final JMenu menuItemAbout;
    private final JMenu menuItemHeart;

    @Getter
    JCheckBoxMenuItem lockingMenuItem;

    public ViewTopMenu(@NonNull final JMenu menuItemMain,
                       @NonNull final JMenu menuItemAbout,
                       @NonNull final JMenu menuItemHeart) {
        this.menuItemMain = menuItemMain;
        this.menuItemAbout = menuItemAbout;
        this.menuItemHeart = menuItemHeart;
    }

    public void initUpperMenuBar() {
        if (OperatingSystemUtils.getOperatingSystemType() == OperatingSystemUtils.OperatingSystemType.WINDOWS) {
            lockingMenuItem = new JCheckBoxMenuItem("Toggle Locking", false);
            menuItemMain.add(lockingMenuItem);
        }

        JCheckBoxMenuItem alwaysOnTopCheckBoxMenuItem = new JCheckBoxMenuItem("Always on top", Application.getContext().getModelSettings().isAlwaysOnTop());
        AlwaysOnTopActionListener alwaysOnTopActionListener = new AlwaysOnTopActionListener();
        alwaysOnTopCheckBoxMenuItem.addActionListener(alwaysOnTopActionListener);
        menuItemMain.add(alwaysOnTopCheckBoxMenuItem);
        menuItemMain.add(new JSeparator());

        JMenu settingsMenu = new JMenu("Settings");

        final var flowMenuItem = getFlowMenuItem();
        settingsMenu.add(flowMenuItem);

        final var themesMenuItem = getThemesMenuItem();
        settingsMenu.add(themesMenuItem);

        if (OperatingSystemUtils.getOperatingSystemType() != OperatingSystemUtils.OperatingSystemType.WINDOWS) {
            final var pathMenuItem = getPathMenuItem();
            settingsMenu.add(pathMenuItem);
        }

        menuItemMain.add(settingsMenu);
        menuItemMain.add(new JSeparator());

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ExitActionListener());
        menuItemMain.add(exitMenuItem);

        menuItemAbout.addMouseListener(new DialogAboutActionListener());
        menuItemHeart.addMouseListener(new DialogHeartActionListener());
    }

    private static JMenu getFlowMenuItem() {
        JMenu flowMenuItem = new JMenu("Flow");

        JCheckBoxMenuItem copyToClipboardCheckBoxMenuItem = new JCheckBoxMenuItem("1. Copy keep to clipboard", Application.getContext().getModelSettings().isCopyToClipboard());
        JCheckBoxMenuItem focusOnTargetWindowCheckBoxMenuItem = new JCheckBoxMenuItem("2. Focus on target window and paste", Application.getContext().getModelSettings().isFocusOnWindowAndPaste());
        JCheckBoxMenuItem autoEnterCheckBoxMenuItem = new JCheckBoxMenuItem("3. Press 'Enter'", Application.getContext().getModelSettings().isPressEnterAfterPaste());


        CopyToClipboardActionListener copyToClipboardActionListener = new CopyToClipboardActionListener(autoEnterCheckBoxMenuItem, focusOnTargetWindowCheckBoxMenuItem);
        copyToClipboardCheckBoxMenuItem.addActionListener(copyToClipboardActionListener);
        flowMenuItem.add(copyToClipboardCheckBoxMenuItem);

        FocusOnTargetWindowActionListener focusOnTargetWindowActionListener = new FocusOnTargetWindowActionListener(copyToClipboardCheckBoxMenuItem, autoEnterCheckBoxMenuItem);
        focusOnTargetWindowCheckBoxMenuItem.addActionListener(focusOnTargetWindowActionListener);
        flowMenuItem.add(focusOnTargetWindowCheckBoxMenuItem);

        AutoEnterActionListener autoEnterActionListener = new AutoEnterActionListener(copyToClipboardCheckBoxMenuItem, focusOnTargetWindowCheckBoxMenuItem);
        autoEnterCheckBoxMenuItem.addActionListener(autoEnterActionListener);
        flowMenuItem.add(autoEnterCheckBoxMenuItem);
        return flowMenuItem;
    }

    private static JMenuItem getPathMenuItem() {
        JMenuItem pathMenuItem = new JMenuItem("$PATH");
        PathMenuItemActionListener pathMenuItemActionListener = new PathMenuItemActionListener();
        pathMenuItem.addActionListener(pathMenuItemActionListener);
        return pathMenuItem;
    }

    private static JMenu getThemesMenuItem() {
        JMenu themesMenuItem = new JMenu("Themes");
        ButtonGroup themesRadioButtonGroup = new ButtonGroup();
        ThemesMenuItemActionListener themesMenuItemActionListener = new ThemesMenuItemActionListener();
        JRadioButtonMenuItem darkModeRadioMenuItem = new JRadioButtonMenuItem("Dark");
        darkModeRadioMenuItem.addActionListener(themesMenuItemActionListener);
        JRadioButtonMenuItem lightModeRadioMenuItem = new JRadioButtonMenuItem("Light");
        lightModeRadioMenuItem.addActionListener(themesMenuItemActionListener);
        themesRadioButtonGroup.add(darkModeRadioMenuItem);
        themesRadioButtonGroup.add(lightModeRadioMenuItem);

        if (Application.getContext().getModelSettings().getTheme().getName().equals(FlatMacDarkLaf.NAME)) {
            darkModeRadioMenuItem.setSelected(true);
        } else {
            lightModeRadioMenuItem.setSelected(true);
        }

        themesMenuItem.add(darkModeRadioMenuItem);
        themesMenuItem.add(lightModeRadioMenuItem);
        return themesMenuItem;
    }

}

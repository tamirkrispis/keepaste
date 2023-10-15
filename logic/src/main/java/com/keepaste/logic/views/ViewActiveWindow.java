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

package com.keepaste.logic.views;

import com.keepaste.logic.models.Model;
import com.keepaste.logic.models.ModelActiveWindow;
import com.keepaste.logic.models.WindowInformation;
import lombok.NonNull;
import javax.swing.*;
import java.awt.*;

/**
 * This View class shows the current active window on the status bar.
 */
public class ViewActiveWindow implements View {

    final JLabel statusLabelProcessName;
    final JLabel statusLabelWindowTitle;

    /**
     * Constructor.
     *
     * @param statusLabelProcessName the window status label to show the process name.
     * @param statusLabelWindowTitle the window status label to show the window title.
     */
    public ViewActiveWindow(@NonNull final JLabel statusLabelProcessName,
                            @NonNull final JLabel statusLabelWindowTitle) {
        this.statusLabelProcessName = statusLabelProcessName;
        this.statusLabelWindowTitle = statusLabelWindowTitle;
    }

    @Override
    public void updateObserver(Model model) {
        // active window has changed, we should updateObserver the GUI (relevant component we got)
        EventQueue.invokeLater(() -> {
            WindowInformation activeWindow = ((ModelActiveWindow) model).getActiveWindow();
            String processName = getAppName((ModelActiveWindow) model);
            statusLabelProcessName.setText(processName);
            statusLabelWindowTitle.setText(activeWindow.getText());
        });
    }

    private String getAppName(ModelActiveWindow modelActiveWindow) {
        WindowInformation activeWindow = (modelActiveWindow).getActiveWindow();
        int lastIndexOfSlash = activeWindow.getApp().lastIndexOf('\\') == -1 ? 0 : activeWindow.getApp().lastIndexOf('\\') + 1;
        String appName = activeWindow.getApp().substring(lastIndexOfSlash);
        if (modelActiveWindow.isLockedOnWindow()) {
            appName += " (locked)";
        }
        return appName;
    }
}

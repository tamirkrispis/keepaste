package com.keepaste.logic.actionlisteners.topmenu;

import com.keepaste.logic.Application;
import com.keepaste.logic.models.KeepsGroup;
import com.keepaste.logic.utils.GuiUtils;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Log4j2
public class PathMenuItemActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Application.getContext().getGui().tree.clearSelection();
        String pathValue = JOptionPane.showInputDialog(Application.getContext().getGui(), "Value of the PATH environment variable to be used when running parameter commands", Application.getContext().getModelSettings().getPath());
        if (pathValue != null && !pathValue.isEmpty()) {
            Application.getContext().getModelSettings().setPath(pathValue);
            log.debug("Settings - path set to [{}]", pathValue);
        } else {
            log.debug("Settings - setting path cancelled");
        }
    }
}

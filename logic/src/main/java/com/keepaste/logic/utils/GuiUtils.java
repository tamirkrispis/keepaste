package com.keepaste.logic.utils;

import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public final class GuiUtils {

    private GuiUtils() {
        // private utility class constructor
    }

    /**
     * Constructor.
     *
     * @param dialog The {@link JDialog} to show
     */
    public static void showDialogOnCenterScreen(@NonNull final JDialog dialog) {
        final Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);
        dialog.setVisible(true);
    }

    public static void initHyperlinkLabel(JLabel label, String url) {
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        URI uri = new URI(url);
                        Desktop.getDesktop().browse(uri);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}

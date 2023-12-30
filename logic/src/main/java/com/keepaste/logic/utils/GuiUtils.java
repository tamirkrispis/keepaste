package com.keepaste.logic.utils;

import com.keepaste.logic.Application;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

@Log4j2
public final class GuiUtils {

    private GuiUtils() {
        // private utility class constructor
    }

    /**
     * Constructor.
     *
     * @param dialog The {@code JDialog} to show
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
                    WebUtils.browseTo(url);
                }
            }
        });
    }

    public static void showTargetWindowLabelMessage(String messageToShow, int showDurationInSeconds) {
        new Thread(() -> {
            Application.getContext().getGui().labelTargetWindow.setText(messageToShow);
            try {
                TimeUnit.SECONDS.sleep(showDurationInSeconds);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Application.getContext().getGui().labelTargetWindow.setText(Application.getContext().getModelActiveWindow().getActiveWindowAppName());
        }).start();
    }
}

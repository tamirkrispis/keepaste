package com.keepaste.logic.actionlisteners.dialogkeep;

import lombok.Getter;
import lombok.NonNull;

import javax.swing.JTable;
import java.awt.event.ActionListener;

public abstract class BaseDialogKeepActionListener implements ActionListener {

    /**
     * the {@link JTable} to show the parameters.
     */
    @Getter
    private final JTable table;

    BaseDialogKeepActionListener(@NonNull final JTable table) {
        this.table = table;
    }
}

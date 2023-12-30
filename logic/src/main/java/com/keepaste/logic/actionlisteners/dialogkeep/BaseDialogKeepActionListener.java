package com.keepaste.logic.actionlisteners.dialogkeep;

import com.keepaste.gui.DialogKeep;
import lombok.NonNull;
import java.awt.event.ActionListener;

public abstract class BaseDialogKeepActionListener implements ActionListener {

    protected final DialogKeep dialogKeep;

    BaseDialogKeepActionListener(@NonNull final DialogKeep dialogKeep) {
        this.dialogKeep = dialogKeep;
    }
}

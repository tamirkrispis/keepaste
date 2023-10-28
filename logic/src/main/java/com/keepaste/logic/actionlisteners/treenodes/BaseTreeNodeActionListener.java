package com.keepaste.logic.actionlisteners.treenodes;

import com.keepaste.logic.views.ViewTree;
import lombok.Getter;
import lombok.NonNull;

import java.awt.event.ActionListener;

public abstract class BaseTreeNodeActionListener implements ActionListener {

    @Getter
    private final ViewTree viewTree;

    protected BaseTreeNodeActionListener(@NonNull final ViewTree viewTree) {
        this.viewTree = viewTree;
    }
}

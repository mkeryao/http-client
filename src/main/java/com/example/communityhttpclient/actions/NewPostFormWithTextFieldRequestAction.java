package com.example.communityhttpclient.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class NewPostFormWithTextFieldRequestAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Messages.showMessageDialog(e.getProject(), "New POST Form with a Text Field action triggered!", "New Request", Messages.getInformationIcon());
    }
}

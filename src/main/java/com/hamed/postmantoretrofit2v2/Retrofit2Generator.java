package com.hamed.postmantoretrofit2v2;

import com.hamed.postmantoretrofit2v2.forms.JsonDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

public class Retrofit2Generator extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        Project project = e.getData(PlatformDataKeys.PROJECT);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        JsonDialog jsonDialog = new JsonDialog(project, editor);
        jsonDialog.pack();
        jsonDialog.setTitle("Postman To Retrofit2 V2");
        jsonDialog.setSize(600, 400);
        jsonDialog.setLocationRelativeTo(null);
        jsonDialog.setVisible(true);
    }
}

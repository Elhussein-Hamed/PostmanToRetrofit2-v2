package com.hamed.postmantoretrofit2v2.utils;

import com.hamed.postmantoretrofit2v2.Constants;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.Collection;

public class ProjectUtils {

    public static boolean restartIde()
    {
        boolean result = Messages.showYesNoDialog(
                "Would you like to restart the IDE?",
                "Restart?",
                "Restart",
                "Cancel",
                Messages.getWarningIcon()
        ) == Messages.YES;

        if (result) {
            final ApplicationEx app = (ApplicationEx) ApplicationManager.getApplication();

            app.restart(true);
        }

        return false;
    }

    public static ArrayList<ClassInfo> getClassesInDirectory(Project project, VirtualFile directory, ArrayList<ClassInfo> originalList)
    {
        ArrayList<ClassInfo> javaFilesList = new ArrayList<>();
        for (int i = 0; i < Constants.supportedClassFileExtensions.size(); i++) {

            Collection<VirtualFile> files = FilenameIndex.getAllFilesByExt(project, Constants.supportedClassFileExtensions.get(i), GlobalSearchScope.projectScope(project));
            for (VirtualFile file : files) {
                if (file.getParent().getPath().equals(directory.getPath())) {
                    ClassInfo classInfo = new ClassInfo(file.getNameWithoutExtension(), file.getExtension());
                    if (!originalList.contains(classInfo))
                        javaFilesList.add(classInfo);
                }
            }
        }
        return javaFilesList;
    }
}

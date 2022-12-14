package com.hamed.postmantoretrofit2v2.utils;

import com.hamed.postmantoretrofit2v2.Constants;
import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packageDependencies.ForwardDependenciesBuilder;
import com.intellij.psi.PsiFile;

import java.util.ArrayList;
import java.util.List;

public class ProjectUtils {

    public static void restartIde()
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
    }

    public static ArrayList<ClassInfo> getClassesInDirectory(Project project, VirtualFile directory, ArrayList<ClassInfo> originalList)
    {
        ArrayList<ClassInfo> javaFilesList = new ArrayList<>();
        ForwardDependenciesBuilder forwardDependenciesBuilder = new ForwardDependenciesBuilder(project, new AnalysisScope(project, List.of(directory)));
        System.out.println("Analysing classes directory...");
        forwardDependenciesBuilder.analyze();
        System.out.println("Done analysing");
        ArrayList<PsiFile> list = new ArrayList<>(forwardDependenciesBuilder.getDirectDependencies().keySet());
        for (PsiFile f : list) {
            String filename = f.getVirtualFile().getNameWithoutExtension();
            String extension = f.getVirtualFile().getExtension();

            if (Constants.supportedClassFileExtensions.contains(extension)) {
                ClassInfo classInfo = new ClassInfo(filename, extension);
                if (!originalList.contains(classInfo))
                    javaFilesList.add(classInfo);
            }
        }
        return javaFilesList;
    }
}

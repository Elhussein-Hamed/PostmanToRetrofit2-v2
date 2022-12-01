package com.hamed.postmantoretrofit2v2.eventlisteners;

import com.hamed.postmantoretrofit2v2.PluginService;
import com.hamed.postmantoretrofit2v2.PluginState;
import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packageDependencies.ForwardDependenciesBuilder;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MyProjectStartupListener implements ProjectManagerListener {
    @Override
    public void projectOpened(@NotNull Project project) {
        ProjectManagerListener.super.projectOpened(project);

        PluginState state = PluginService.getInstance(project).getState();
        if(!state.getResponseTypeClassesDirectory().isEmpty())
        {
            VirtualFile file = LocalFileSystem.getInstance().findFileByPath(state.getResponseTypeClassesDirectory());
            System.out.println("Selected classes directory virtual file: " + file.getPath());
            ArrayList<String> classesList = new ArrayList<>();

            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                DumbService dumbService = DumbService.getInstance(project);
                classesList.addAll(dumbService.runReadActionInSmartMode(() -> {

                    ArrayList<String> javaFilesList = new ArrayList<>();
                    ForwardDependenciesBuilder forwardDependenciesBuilder = new ForwardDependenciesBuilder(project, new AnalysisScope(project, List.of(file)));
                    forwardDependenciesBuilder.analyze();
                    ArrayList<PsiFile> list = new ArrayList<>(forwardDependenciesBuilder.getDirectDependencies().keySet());
                    for (PsiFile f : list)
                        if (f.getName().contains(".java") && !classesList.contains(f.getName()))
                            javaFilesList.add(f.getName().replace(".java", ""));
                    return javaFilesList;
                }));

                state.setResponseTypeClassesList(classesList);
            });
        }
    }
}

package com.hamed.postmantoretrofit2v2.eventlisteners;

import com.hamed.postmantoretrofit2v2.pluginstate.PluginService;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginState;
import com.hamed.postmantoretrofit2v2.utils.ClassInfo;
import com.hamed.postmantoretrofit2v2.utils.ProjectUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyProjectStartupListener implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        PluginState state = PluginService.getInstance(project).getState();
        assert state != null;

        System.out.println("Re-calculate the ResponseTypeClassesList");
        if(!state.getReturnTypeClassesDirectory().isEmpty())
        {
            ArrayList<ClassInfo> classesList = new ArrayList<>();

            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                DumbService dumbService = DumbService.getInstance(project);
                classesList.addAll(dumbService.runReadActionInSmartMode(() -> {
                    VirtualFile directory = LocalFileSystem.getInstance().findFileByPath(state.getReturnTypeClassesDirectory());
                    return ProjectUtils.getClassesInDirectory(project, directory, classesList);
                }));

                state.setReturnTypeClassInfoList(classesList);
            });
        }
    }
}

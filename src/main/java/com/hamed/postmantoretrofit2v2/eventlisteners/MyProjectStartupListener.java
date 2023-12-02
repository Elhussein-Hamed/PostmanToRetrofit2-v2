package com.hamed.postmantoretrofit2v2.eventlisteners;

import com.hamed.postmantoretrofit2v2.pluginstate.PluginService;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginState;
import com.hamed.postmantoretrofit2v2.utils.ClassInfo;
import com.hamed.postmantoretrofit2v2.utils.ProjectUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class MyProjectStartupListener implements ProjectActivity {

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
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
                System.out.println("MyProjectStartupListener - classesList in " + state.getReturnTypeClassesDirectory()
                        + ": " + classesList);
                state.setReturnTypeClassInfoList(classesList);
            });
        }
        return null;
    }
}

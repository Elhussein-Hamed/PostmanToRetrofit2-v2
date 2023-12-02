package com.hamed.postmantoretrofit2v2.eventlisteners;

import com.hamed.postmantoretrofit2v2.Constants;
import com.hamed.postmantoretrofit2v2.messaging.MessageBroker;
import com.hamed.postmantoretrofit2v2.messaging.NewClassInfoAddedMessage;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginService;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginState;
import com.hamed.postmantoretrofit2v2.utils.ClassInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyBulkFileListener implements BulkFileListener {

    private final Project mProject;

    public MyBulkFileListener(Project project) {
        mProject = project;
    }

    @Override
    public void before(@NotNull List<? extends @NotNull VFileEvent> events) {
        BulkFileListener.super.before(events);
    }

    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {

        if (StartupManager.getInstance(mProject).postStartupActivityPassed())
        {
            ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(mProject).getFileIndex();

            if (mProject.getProjectFile() == null || projectFileIndex.getContentRootForFile(mProject.getProjectFile()) == null)
            {
                System.out.println("The project file doesn't seem to exit yet, " +
                        "hence it cannot be used to deduce the project directory. " +
                        "The following events are ignored: " + events);
                return;
            }

            VirtualFile projectRootDir = projectFileIndex.getContentRootForFile(mProject.getProjectFile());
            assert projectRootDir != null;
            events.stream()
                    .filter(event -> event.getFile() != null
                            && (projectFileIndex.isInContent(event.getFile())  // Ensure the file belongs to this project
                            || event.getFile().getPath().contains(projectRootDir.getPath()) // Or ensure the file is under the project directory or one of its subdirectories
                            && event.getFile().getExtension() != null  // Ensure the file has an extension
                            && Constants.supportedClassFileExtensions.contains(event.getFile().getExtension()))) // Ensure the extension is one of the supported extensions
                    .collect(Collectors.toList())
                    .forEach(this::handleEvent);

        }
    }

    private void handleEvent(VFileEvent event) {

        if (!(event instanceof VFileCreateEvent) && !(event instanceof VFileDeleteEvent))
            return;

        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;
        VirtualFile responseTypeClassesDirVirtualFile = LocalFileSystem.getInstance().findFileByPath(state.getReturnTypeClassesDirectory());
        assert responseTypeClassesDirVirtualFile != null;

        if (event instanceof VFileCreateEvent) {
            System.out.println("Handle event: " + event);
            assert event.getFile() != null;

            // Check if the file is under the directory or one of its subdirectories
            if (event.getFile().getParent().getPath().contains(responseTypeClassesDirVirtualFile.getPath())) {
                VirtualFile file = event.getFile();
                String filename = file.getNameWithoutExtension();
                String extension = file.getExtension();
                ClassInfo classInfo = new ClassInfo(filename, extension);

                System.out.println("Added file: " + event.getFile().toString());
                ArrayList<ClassInfo> classesList = state.getReturnTypeClassInfoList();
                if (!classesList.contains(classInfo)) {
                    classesList.add(classInfo);
                    state.setReturnTypeClassInfoList(classesList);
                    MessageBroker.getInstance().sendMessage(new NewClassInfoAddedMessage(classInfo));
                }
            }
        }
        else {  // event instanceof VFileDeleteEvent
            System.out.println("Handle event: " + event);

            // Check if the file is under the directory or one of its subdirectories
            if (event.getFile().getParent().getPath().contains(responseTypeClassesDirVirtualFile.getPath())) {
                VirtualFile file = event.getFile();
                String filename = file.getNameWithoutExtension();
                String extension = file.getExtension();
                ClassInfo classInfo = new ClassInfo(filename, extension);

                System.out.println("Removed file: " + filename);
                ArrayList<ClassInfo> classesList = state.getReturnTypeClassInfoList();
                if (classesList.contains(classInfo)) {
                    System.out.println("classesList.contains: " + classInfo);
                    classesList.remove(classInfo);
                    state.setReturnTypeClassInfoList(classesList);
                }
            }
        }  // Not interested in the other events
    }
}

package com.hamed.postmantoretrofit2v2.eventlisteners;

import com.hamed.postmantoretrofit2v2.PluginService;
import com.hamed.postmantoretrofit2v2.PluginState;
import com.hamed.postmantoretrofit2v2.messaging.MessageBroker;
import com.hamed.postmantoretrofit2v2.messaging.NewJavaFileMessage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
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
        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(mProject).getFileIndex();
        events = events.stream()
                .filter(event -> event.getFile() != null
                        && (projectFileIndex.isInContent(event.getFile())
                        || event.getFile().getPath().contains(projectFileIndex.getContentRootForFile(mProject.getProjectFile()).getPath()))
                        && event.getFile().getName().contains(".java"))
                .collect(Collectors.toList());

        events.forEach(this::handleEvent);

    }

    private void handleEvent(VFileEvent event) {
        PluginState state = PluginService.getInstance(mProject).getState();
        VirtualFile responseTypeClassesDirVirtualFile = LocalFileSystem.getInstance().findFileByPath(state.getResponseTypeClassesDirectory());

        if (event instanceof VFileCreateEvent) {
            System.out.println("Handle event: " + event);
            if (event.getFile().getParent().equals(responseTypeClassesDirVirtualFile)) {
                String filename = event.getFile().getName().replace(".java", "");
                System.out.println("Added file: " + event.getFile().toString());
                ArrayList<String> classesList = state.getResponseTypeClassesList();
                if (!classesList.contains(filename)) {
                    classesList.add(filename);
                    state.setResponseTypeClassesList(classesList);
                    MessageBroker.getInstance().sendMessage(new NewJavaFileMessage(filename));
                }
            }
        } else if (event instanceof VFileDeleteEvent) {
            System.out.println("Handle event: " + event);
            if (event.getFile().getParent().equals(responseTypeClassesDirVirtualFile)) {
                String filename = event.getFile().getName().replace(".java", "");
                System.out.println("Removed file: " + event.getFile().toString());
                ArrayList<String> classesList = state.getResponseTypeClassesList();
                if (classesList.contains(filename)) {
                    classesList.remove(filename);
                    state.setResponseTypeClassesList(classesList);
                }
            }
        } else {
            // Not interested in the other events
        }
    }
}

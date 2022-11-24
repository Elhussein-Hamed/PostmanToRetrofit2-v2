package com.hamed.postmantoretrofit2v2.eventlisteners;

import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiTreeChangeListener;
import org.jetbrains.annotations.NotNull;

public class MyPsiTreeChangeListener implements PsiTreeChangeListener {

    private final FileCreatedListener fileCreatedListener;

    public MyPsiTreeChangeListener(FileCreatedListener listener) {
        this.fileCreatedListener = listener;
    }

    @Override
    public void beforeChildAddition(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildMovement(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildrenChange(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforePropertyChange(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        if (event.getFile() != null) {
            System.out.println("childAdded File: " + event.getFile().toString());
            fileCreatedListener.onFileCreated(event.getFile());
        }
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent event) {

    }
}

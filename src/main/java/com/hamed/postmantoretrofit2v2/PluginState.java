package com.hamed.postmantoretrofit2v2;

import java.util.ArrayList;

public class PluginState {

    private String responseType;
    private Boolean isLanguageJava;
    private String fileSelectionDir;
    private Boolean promptToSelectClassForResponseType;
    private ArrayList<String> javaFileNamesList;
    private String javaFilesDirectory;

    public PluginState() {
        this.responseType = "";
        this.isLanguageJava = true;
        this.fileSelectionDir = "";
        this.promptToSelectClassForResponseType = false;
        this.javaFileNamesList = new ArrayList<>();
        this.javaFilesDirectory = "";
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public Boolean getLanguageJava() {
        return isLanguageJava;
    }

    public void setLanguageJava(Boolean languageJava) {
        isLanguageJava = languageJava;
    }

    public String getFileSelectionDir() {
        return fileSelectionDir;
    }

    public void setFileSelectionDir(String fileSelectionDir) {
        this.fileSelectionDir = fileSelectionDir;
    }

    public Boolean getPromptToSelectClassForResponseType() {
        return promptToSelectClassForResponseType;
    }

    public void setPromptToSelectClassForResponseType(Boolean promptToSelectClassForResponseType) {
        this.promptToSelectClassForResponseType = promptToSelectClassForResponseType;
    }

    public ArrayList<String> getJavaFileNamesList() { return javaFileNamesList; }

    public void setJavaFileNamesList(ArrayList<String> javaFileNamesList) { this.javaFileNamesList = javaFileNamesList; }

    public String getJavaFilesDirectory() { return javaFilesDirectory; }

    public void setJavaFilesDirectory(String javaFilesDirectory) { this.javaFilesDirectory = javaFilesDirectory; }

}

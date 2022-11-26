package com.hamed.postmantoretrofit2v2;

import java.util.ArrayList;

public class PluginState {

    private String responseType;
    private Boolean isLanguageJava;
    private String lastVisitedDir;
    private Boolean promptToSelectClassForResponseType;
    private ArrayList<String> responseTypeClassesList;
    private String responseTypeClassesDirectory;

    public PluginState() {
        this.responseType = "";
        this.isLanguageJava = true;
        this.lastVisitedDir = "";
        this.promptToSelectClassForResponseType = false;
        this.responseTypeClassesList = new ArrayList<>();
        this.responseTypeClassesDirectory = "";
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

    public String getLastVisitedDir() {
        return lastVisitedDir;
    }

    public void setLastVisitedDir(String lastVisitedDir) {
        this.lastVisitedDir = lastVisitedDir;
    }

    public Boolean getPromptToSelectClassForResponseType() {
        return promptToSelectClassForResponseType;
    }

    public void setPromptToSelectClassForResponseType(Boolean promptToSelectClassForResponseType) {
        this.promptToSelectClassForResponseType = promptToSelectClassForResponseType;
    }

    public ArrayList<String> getResponseTypeClassesList() { return responseTypeClassesList; }

    public void setResponseTypeClassesList(ArrayList<String> responseTypeClassesList) { this.responseTypeClassesList = responseTypeClassesList; }

    public String getResponseTypeClassesDirectory() { return responseTypeClassesDirectory; }

    public void setResponseTypeClassesDirectory(String responseTypeClassesDirectory) { this.responseTypeClassesDirectory = responseTypeClassesDirectory; }

}

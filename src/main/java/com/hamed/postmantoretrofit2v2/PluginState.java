package com.hamed.postmantoretrofit2v2;

public class PluginState {

    private String rxJavaResponseType;
    private Boolean isLanguageJava;
    private String fileSelectionDir;

    public PluginState() {
        this.rxJavaResponseType = "";
        this.isLanguageJava = true;
        this.fileSelectionDir = "";
    }

    public String getRxJavaResponseType() {
        return rxJavaResponseType;
    }

    public void setRxJavaResponseType(String rxJavaResponseType) {
        this.rxJavaResponseType = rxJavaResponseType;
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
}

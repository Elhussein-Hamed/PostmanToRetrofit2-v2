package com.hamed.postmantoretrofit2v2;

public class PluginState {

    private String responseType;
    private Boolean isLanguageJava;
    private String fileSelectionDir;

    public PluginState() {
        this.responseType = "";
        this.isLanguageJava = true;
        this.fileSelectionDir = "";
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
}

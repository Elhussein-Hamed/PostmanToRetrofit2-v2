package com.hamed.postmantoretrofit2v2.pluginstate;

import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.converters.ClassInfoConverter;
import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums.Language;
import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums.ReturnTypeRadioButton;
import com.hamed.postmantoretrofit2v2.utils.ClassInfo;
import com.intellij.util.xmlb.annotations.OptionTag;

import java.util.ArrayList;

public class PluginState {

    private String lastVisitedDir;
    private Boolean promptToSelectClassForReturnType;
    private ReturnTypeRadioButton returnTypeRadioButton;
    private String returnType;
    @OptionTag(converter = ClassInfoConverter.class)
    private ArrayList<ClassInfo> returnTypeClassInfoList;
    private String returnTypeClassesDirectory;
    private Language language;

    public PluginState() {
        this.lastVisitedDir = "";
        this.promptToSelectClassForReturnType = false;
        this.returnType = "";
        this.returnTypeClassInfoList = new ArrayList<>();
        this.returnTypeClassesDirectory = "";
        this.language = Language.JAVA;
        this.returnTypeRadioButton = ReturnTypeRadioButton.BUTTON_RETROFIT_RAW_TYPES;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public ReturnTypeRadioButton getReturnTypeRadioButton() {
        return returnTypeRadioButton;
    }

    public void setReturnTypeRadioButton(ReturnTypeRadioButton returnTypeRadioButton) {
        this.returnTypeRadioButton = returnTypeRadioButton;
    }

    public String getLastVisitedDir() {
        return lastVisitedDir;
    }

    public void setLastVisitedDir(String lastVisitedDir) {
        this.lastVisitedDir = lastVisitedDir;
    }

    public Boolean getPromptToSelectClassForReturnType() {
        return promptToSelectClassForReturnType;
    }

    public void setPromptToSelectClassForReturnType(Boolean promptToSelectClassForReturnType) {
        this.promptToSelectClassForReturnType = promptToSelectClassForReturnType;
    }

    public ArrayList<ClassInfo> getReturnTypeClassInfoList() { return returnTypeClassInfoList; }

    public void setReturnTypeClassInfoList(ArrayList<ClassInfo> returnTypeClassInfoList) {
        this.returnTypeClassInfoList = returnTypeClassInfoList;
    }

    public String getReturnTypeClassesDirectory() { return returnTypeClassesDirectory; }

    public void setReturnTypeClassesDirectory(String returnTypeClassesDirectory) { this.returnTypeClassesDirectory = returnTypeClassesDirectory; }

}

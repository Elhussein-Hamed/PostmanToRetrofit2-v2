package com.hamed.postmantoretrofit2v2.utils;

import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums.Language;

import java.util.Objects;

/**
 * Model class used by Gson to convert to/from Json when saving the plugin state
 */
@SuppressWarnings("unused")
public class ClassInfo {
    private String className;
    private String classExtension;

    public ClassInfo(String className, String classExtension) {
        this.className = className;
        this.classExtension = classExtension;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassExtension() {
        return classExtension;
    }

    public void setClassExtension(String classExtension) {
        this.classExtension = classExtension;
    }

    public Language getLanguage()
    {
        if (classExtension.equals("java"))
            return Language.JAVA;
        else
            return Language.KOTLIN;
    }

    @Override
    public String toString() {
        return className + " (" + classExtension + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassInfo classInfo = (ClassInfo) o;
        return Objects.equals(className, classInfo.className) && Objects.equals(classExtension, classInfo.classExtension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, classExtension);
    }
}

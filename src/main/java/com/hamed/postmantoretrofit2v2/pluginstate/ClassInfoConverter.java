package com.hamed.postmantoretrofit2v2.pluginstate;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.hamed.postmantoretrofit2v2.utils.ClassInfo;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ClassInfoConverter extends Converter<ArrayList<ClassInfo>> {

    private final Type listType = new TypeToken<ArrayList<ClassInfo>>() {}.getType();

    public ArrayList<ClassInfo> fromString(@NotNull String value) {
        return new Gson().fromJson(value, listType);
    }

    public String toString(@NotNull ArrayList<ClassInfo> value) {
        return new Gson().toJson(value);
    }
}

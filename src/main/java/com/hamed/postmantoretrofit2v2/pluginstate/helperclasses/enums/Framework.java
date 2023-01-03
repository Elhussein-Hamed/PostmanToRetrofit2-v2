package com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums;

import java.util.Arrays;

public enum Framework {

    NONE("None"), 
    NONE_RECORDS("None (records)"),
    LOMBOK("Lombok"),
    GSON("GSON"),
    GSON_RECORDS("GSON (records)"),
    JACKSON("Jackson"),
    JACKSON_RECORDS("Jackson (records)"),
    LOGAN_SQUARE("Logan Square"),
    LOGAN_SQUARE_RECORDS("Logan Square (records)"),
    MOSHI("Moshi"),
    MOSHI_RECORDS("Moshi (records)"),
    FASTJSON("FastJson"), 
    FASTJSON_RECORDS("FastJson (records)"),
    AUTO_VALUE("AutoValue");
    
    private final String frameworkString;
    
    Framework(String frameworkString)
    {
        this.frameworkString = frameworkString;
    }

    @Override
    public String toString() {
        return this.frameworkString;
    }

    public static String[] stringValues()
    {
        return Arrays.stream(Framework.values()).map(Framework::toString).toArray(String[]::new);
    }
}

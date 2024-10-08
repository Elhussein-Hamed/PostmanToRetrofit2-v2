package com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public static Framework fromString(String frameworkString) {

        List<Framework> list = Arrays.stream(Framework.values())
                .filter(framework -> framework.toString().equals(frameworkString))
                .collect(Collectors.toList());

        assert list.size() == 1;
        return list.get(0);
    }
}

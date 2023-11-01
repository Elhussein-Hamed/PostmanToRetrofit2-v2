package com.hamed.postmantoretrofit2v2.utils;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {

   private static final String WORD_SEPARATOR = " ";

    public static String convertToTitleCase(@NotNull String text) {
        if (!text.contains(" ")) {
            if (StringUtil.isUpperCase(text))
                text = text.toLowerCase();
            return StringUtil.toTitleCase(text);
        }

        return Arrays
                .stream(text.split(WORD_SEPARATOR))
                .map(word -> word.isEmpty()
                        ? word
                        : Character.toTitleCase(word.charAt(0)) + word
                        .substring(1)
                        .toLowerCase())
                .collect(Collectors.joining(WORD_SEPARATOR));
    }

    public static String skipQuotes(String src)
    {
        StringBuilder output = new StringBuilder(src);
        ArrayList<Integer> insertAtIndexList = new ArrayList<>();
        for(int i = 0; i < src.length(); i++)
        {
            if (src.charAt(i) == '\"')
                insertAtIndexList.add(i);
        }

        for (int i = 0; i < insertAtIndexList.size(); i++) {
            // Whenever a '\' is inserted ensure that the index of the next '"' occurrence is incremented.
            output.insert(insertAtIndexList.get(i) + i, "\\");
        }

        return output.toString();
    }

    public static String getIndentation(int size)
    {
        return " ".repeat(size);
    }

    public static String highlightReturnTypeWithHashes(String returnType)
    {
        return "##" + returnType + "##";
    }

    public static String removeHashesAroundReturnType(String method)
    {
        return method.replaceAll("#", "");
    }

    public static ArrayList<String> extractParamsFromUrlApiPath(String urlApiPath)
    {
        ArrayList<String> params = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\{(\\w+)}");
        Matcher matcher = pattern.matcher(urlApiPath);
        if (matcher.find())
        {
            for (int i = 1; i <= matcher.groupCount(); i++)
                params.add(matcher.group(i));
        }

        return params;
    }
}

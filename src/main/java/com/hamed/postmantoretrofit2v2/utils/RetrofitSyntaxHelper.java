package com.hamed.postmantoretrofit2v2.utils;

import com.hamed.postmantoretrofit2v2.pluginstate.Language;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RetrofitSyntaxHelper {

    public static String constructAnnotatedParam(Language language, String annotation, String annotationValue, String parameterName)
    {
        StringBuilder result = new StringBuilder();

        // The annotation is at the beginning for both Java and Kotlin
        result.append(annotation)
                .append("(\"")
                .append(annotationValue)
                .append("\") ");

        if (Language.JAVA == language)
            result.append("String ")
                    .append(parameterName);
        else
            result.append(parameterName)
                    .append(": String");


        return result.toString();
    }

    public static String constructAnnotatedParam(Language language, String annotation, String parameterName)
    {
        StringBuilder result = new StringBuilder();

        // The annotation is at the beginning for both Java and Kotlin
        result.append(annotation)
                .append(" ");

        if (Language.JAVA == language)
            result.append("String ")
                    .append(parameterName);
        else
            result.append(parameterName)
                    .append(": String");


        return result.toString();
    }

    public static String constructMethodSignature(Language language, String returnType, String methodName, String dynamicHeader, String pathParams, String fieldParams, String queryParams, String bodyParam)
    {
        StringBuilder result = new StringBuilder();

        if (Language.KOTLIN == language)
            result.append("fun ")
                    .append(methodName);
        else
            result.append(returnType)
                    .append(" ")
                    .append(methodName);

        String params = Stream.of(dynamicHeader, pathParams, fieldParams, queryParams, bodyParam)
                .filter(str -> !str.isEmpty())
                .collect(Collectors.joining(", "));


        result.append("(")
                .append(params)
                .append(")");

        if (Language.KOTLIN == language)
            result.append(" : ")
                    .append(returnType);
        else
            result.append(";");

        return result.toString();
    }

    // For backward compatibility
    // TODO: Update all the occurrences to provide all the parameters and remove this method
    public static String constructMethodSignature(Language language, String returnType, String methodName, String dynamicHeader, String fieldParams, String queryParams)
    {
        return constructMethodSignature(language, returnType, methodName, dynamicHeader, "", fieldParams, queryParams, "");
    }
}

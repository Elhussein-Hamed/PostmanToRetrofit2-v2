package com.hamed.postmantoretrofit2v2;

import com.esotericsoftware.kryo.util.IntArray;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.ui.Messages;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Utils {

   private static final String WORD_SEPARATOR = " ";

    public static String convertToTitleCase(String text) {
        if (text == null || !text.contains(" ")) {
            return text;
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
        IntArray insertAtIndexList = new IntArray();
        for(int i = 0; i < src.length(); i++)
        {
            if (src.charAt(i) == '\"')
                insertAtIndexList.add(i);
        }

        for (int i = 0; i < insertAtIndexList.size; i++) {
            // Whenever a '\' is inserted ensure that the index of the next '"' occurrence is incremented.
            output.insert(insertAtIndexList.get(i) + i, "\\");
        }

        return output.toString();
    }

    public static void restartIde()
    {
        boolean result = Messages.showYesNoDialog(
                "Would you like to restart the IDE?",
                "Restart?",
                "Restart",
                "Cancel",
                Messages.getWarningIcon()
        ) == Messages.YES;

        if (result) {
            final ApplicationEx app = (ApplicationEx) ApplicationManager.getApplication();

            app.restart(true);
        }
    }
}

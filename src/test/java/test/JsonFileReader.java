package test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonFileReader {
    public static String readJsonFromFile(String filePath)
    {
        try {
            return Files.readString(Path.of(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

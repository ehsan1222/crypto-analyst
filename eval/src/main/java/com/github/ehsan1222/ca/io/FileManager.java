package com.github.ehsan1222.ca.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {

    public String read(Path path) {
        if (Files.notExists(path) || !Files.isReadable(path)) {
            return null;
        }
        try {
            return String.join("", Files.readAllLines(path));
        } catch (IOException e) {
            return null;
        }
    }

}

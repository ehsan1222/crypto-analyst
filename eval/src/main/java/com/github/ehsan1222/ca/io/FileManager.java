package com.github.ehsan1222.ca.io;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public String getMD5Hash(Path path) {
        if (Files.notExists(path) || !Files.isReadable(path)) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(path));
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException | IOException e) {
            // TODO: logger
            return "";
        }
    }
}

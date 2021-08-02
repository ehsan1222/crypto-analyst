package com.github.ehsan1222.ca.io;

import lombok.extern.java.Log;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Log
public class FileManager {

    public String read(Path path) {
        if (Files.notExists(path) || !Files.isReadable(path)) {
            log.warning("path does not accessible " + path);
            return null;
        }
        try {
            return String.join("", Files.readAllLines(path));
        } catch (IOException e) {
            log.warning("error while reading the file " + e.getMessage());
            return null;
        }
    }

    public String getMD5Hash(Path path) {
        if (Files.notExists(path) || !Files.isReadable(path)) {
            log.warning("path does not accessible " + path);
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(path));
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            log.warning("invalid hash algorithm was specified " + e.getMessage());
            return "";
        } catch (IOException e) {
            log.warning("error while hashing the file " + e.getMessage());
            return "";
        }

    }
}

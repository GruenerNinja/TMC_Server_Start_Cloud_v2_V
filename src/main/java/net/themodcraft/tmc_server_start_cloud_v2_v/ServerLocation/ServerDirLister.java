package net.themodcraft.tmcserverstartcloudv2.ServerLocation;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServerDirLister {

    public static List<String> listServerDirectories(String rootDirectoryPath) {
        List<String> serverDirectories = new ArrayList<>();

        try {
            // Walk through the directory structure and collect folder paths
            Files.walk(Paths.get(rootDirectoryPath), FileVisitOption.FOLLOW_LINKS)
                    .filter(Files::isDirectory)
                    .forEach(path -> {
                        // Add directory path to the list
                        serverDirectories.add(path.toString());
                    });
        } catch (IOException e) {
            // Handle any errors
            e.printStackTrace();
        }

        return serverDirectories;
    }
}

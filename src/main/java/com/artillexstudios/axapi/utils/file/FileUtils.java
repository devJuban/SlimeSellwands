package com.artillexstudios.axapi.utils.file;

import com.artillexstudios.axapi.AxPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils {
    private static final FileUtils INSTANCE = new FileUtils();
    private FileUtils() { }
    public static FileUtils getInstance() { return INSTANCE; }
    public File getFolder() { return AxPlugin.getInstance().getDataFolder(); }
    public static void deleteNested(Path path) {
        if (!Files.exists(path)) return;
        try {
            Files.walkFileTree(path, new java.nio.file.SimpleFileVisitor<Path>() {
                @Override public java.nio.file.FileVisitResult visitFile(Path file, java.nio.file.attribute.BasicFileAttributes attrs) throws IOException {
                    Files.deleteIfExists(file); return java.nio.file.FileVisitResult.CONTINUE;
                }
                @Override public java.nio.file.FileVisitResult postVisitDirectory(Path dir, IOException error) throws IOException {
                    if (error != null) throw error;
                    Files.deleteIfExists(dir); return java.nio.file.FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}

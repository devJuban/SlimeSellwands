package com.artillexstudios.axsellwands.utils;

import com.artillexstudios.axsellwands.AxSellwands;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.nio.file.StandardCopyOption;

public class FileUtils {
    public static final Path PLUGIN_DIRECTORY = AxSellwands.getInstance().getDataFolder().toPath();

    public static void copyFromResource(@NotNull String path) {
        try (ZipFile zip = new ZipFile(Paths.get(AxSellwands.getInstance().getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).toFile())) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().startsWith(path + "/")) {
                    if (!entry.getName().endsWith(".yaml") && !entry.getName().endsWith(".yml")) continue;
                    InputStream resource = AxSellwands.getInstance().getResource(entry.getName());
                    if (resource == null)   continue;
                    Path target = PLUGIN_DIRECTORY.resolve(entry.getName());
                    Files.createDirectories(target.getParent());
                    if (Files.exists(target)) continue;
                    try (InputStream stream = resource) {
                        Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }
}

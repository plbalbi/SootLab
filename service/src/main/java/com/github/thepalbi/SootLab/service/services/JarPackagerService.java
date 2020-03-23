package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.services.erros.FileManagerException;
import com.github.thepalbi.SootLab.service.services.erros.PackagerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

@Service
public class JarPackagerService implements PackagerService {

    public static final String JAR_EXTENSION = ".jar";
    private Path tempPackagerDir;

    @Autowired
    public JarPackagerService(TemporaryFileManager temporaryFileManager) throws FileManagerException {
        tempPackagerDir = temporaryFileManager.getDirectory();
    }

    @Override
    public Path pack(File classFilesDirectory) throws PackagerException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        try {
            Path pathToJar = Files.createTempFile(tempPackagerDir, "", JAR_EXTENSION);
            JarOutputStream targetJar = new JarOutputStream(new FileOutputStream(pathToJar.toFile()), manifest);
            recursiveAdd(classFilesDirectory, targetJar, classFilesDirectory);
            targetJar.close();
            return pathToJar;
        } catch (Exception e) {
            throw new PackagerException(e);
        }
    }

    private void recursiveAdd(File directoryOfClassFile, JarOutputStream targetJar, File basePath) throws IOException {
        if (directoryOfClassFile.isDirectory()) {
            // Path is not empty, and it's not the base path of the compiled classes working folder
            if (!directoryOfClassFile.getPath().isEmpty() && !directoryOfClassFile.equals(basePath)) {
                String relativizedPathToDirectory = getRelativizedPathAgainst(directoryOfClassFile, basePath);
                if (!relativizedPathToDirectory.endsWith("/")) relativizedPathToDirectory += "/";
                JarEntry directoryEntry = new JarEntry(relativizedPathToDirectory);
                targetJar.putNextEntry(directoryEntry);
                targetJar.closeEntry();
            }
            for (File nestedDirOrClassFile : directoryOfClassFile.listFiles()) {
                recursiveAdd(nestedDirOrClassFile, targetJar, basePath);
            }
        } else {
            JarEntry jarEntry = new JarEntry(getRelativizedPathAgainst(directoryOfClassFile, basePath));
            targetJar.putNextEntry(jarEntry);
            try (InputStream classFileStream = new BufferedInputStream(new FileInputStream(directoryOfClassFile))) {
                copyWithoutClosing(classFileStream, targetJar);
            }
            targetJar.closeEntry();
        }
    }

    private String getRelativizedPathAgainst(File directoryOfClassFile, File basePath) {
        return basePath.toURI().relativize(directoryOfClassFile.toURI()).getPath();
    }

    private void copyWithoutClosing(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int count = inputStream.read(buffer);
            if (count == -1) break;
            outputStream.write(buffer, 0, count);
        }
    }
}

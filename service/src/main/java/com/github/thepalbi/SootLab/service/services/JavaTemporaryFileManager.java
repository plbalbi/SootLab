package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.services.erros.FileManagerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;

@Service
public class JavaTemporaryFileManager implements TemporaryFileManager {

    final private String fileManagerPrefix;

    // TODO: Implement regular cleaner
    final private Path fileManagerTmpDir;

    @Autowired
    public JavaTemporaryFileManager(
            @Value("#{ @environment['fileManager.temp.prefix'] ?: 'soot-lab-temp-' }") String fileManagerPrefix
    ) throws FileManagerException {
        this.fileManagerPrefix = fileManagerPrefix;
        this.fileManagerTmpDir = this.getDirectory();
    }

    @Override
    public Path getDirectory() throws FileManagerException {
        try {
            return Files.createTempDirectory(fileManagerPrefix);
        } catch (IOException e) {
            throw new FileManagerException();
        }
    }

    @Override
    public Path getFileWithContents(String contents) throws FileManagerException {
        try {
            File sourceFile = Files.createTempFile(fileManagerTmpDir, "", "").toFile();
            FileWriter writer = new FileWriter(sourceFile);
            writer.write(contents);
            writer.close();
            return sourceFile.toPath();
        } catch (IOException e) {
            throw new FileManagerException();
        }
    }
}

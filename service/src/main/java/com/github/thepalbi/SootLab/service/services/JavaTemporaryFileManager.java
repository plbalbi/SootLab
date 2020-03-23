package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.services.erros.FileManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class JavaTemporaryFileManager implements TemporaryFileManager {

    private Logger logger = LoggerFactory.getLogger(JavaTemporaryFileManager.class);

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
    public Path getFileWithContentsNamed(String contents, String name) throws FileManagerException {
        try {
            Path composedSourceFilePath = Paths.get(fileManagerTmpDir.toString(), name);

            logger.info("Writing contents to file {}", composedSourceFilePath.toString());

            File sourceFile = new File(composedSourceFilePath.toUri());
            FileWriter writer = new FileWriter(sourceFile);
            writer.write(contents);
            // TODO: This might end up dangling, add an auto-closing scope
            writer.close();
            return sourceFile.toPath();
        } catch (IOException e) {
            throw new FileManagerException();
        }
    }
}

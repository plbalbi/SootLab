package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.services.erros.FileManagerException;

import java.nio.file.Path;

public interface TemporaryFileManager {
    Path getDirectory() throws FileManagerException;
    Path getFileWithContents(String contents) throws FileManagerException;
}

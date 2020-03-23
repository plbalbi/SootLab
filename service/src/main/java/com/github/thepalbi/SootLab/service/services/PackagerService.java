package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.services.erros.PackagerException;

import java.io.File;
import java.nio.file.Path;

public interface PackagerService {
    Path pack(File classFilesDirectory) throws PackagerException;
}

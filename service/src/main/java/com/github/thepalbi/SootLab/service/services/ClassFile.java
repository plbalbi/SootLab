package com.github.thepalbi.SootLab.service.services;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

// NOTE: Is this necessary?
public class ClassFile {

    private URI uriToFile;

    public ClassFile(URI uriToFile) {
        this.uriToFile = uriToFile;
    }

    public String getPath() {
        return uriToFile.getPath();
    }

    public Path asPath() {
        return Paths.get(uriToFile);
    }
}

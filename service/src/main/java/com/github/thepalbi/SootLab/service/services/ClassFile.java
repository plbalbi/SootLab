package com.github.thepalbi.SootLab.service.services;

import java.net.URI;

// NOTE: Is this necessary?
public class ClassFile {

    private URI uriToFile;

    public ClassFile(URI uriToFile) {
        this.uriToFile = uriToFile;
    }

    public String getPath() {
        return uriToFile.getPath();
    }
}

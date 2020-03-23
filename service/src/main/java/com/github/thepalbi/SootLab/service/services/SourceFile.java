package com.github.thepalbi.SootLab.service.services;

import javax.tools.SimpleJavaFileObject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class SourceFile extends SimpleJavaFileObject {

    private File backingFile;

    public SourceFile(Path filePath) {
        super(filePath.toUri(), Kind.SOURCE);
        this.backingFile = new File(filePath.toUri());
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return new String(Files.readAllBytes(this.backingFile.toPath()), Charset.defaultCharset());
    }
}

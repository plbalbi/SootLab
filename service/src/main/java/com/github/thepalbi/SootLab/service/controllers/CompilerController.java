package com.github.thepalbi.SootLab.service.controllers;

import com.github.thepalbi.SootLab.service.services.CompilerService;
import com.github.thepalbi.SootLab.service.services.SourceFile;
import com.github.thepalbi.SootLab.service.services.TemporaryFileManager;
import com.github.thepalbi.SootLab.service.services.erros.CompilationException;
import com.github.thepalbi.SootLab.service.services.erros.FileManagerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.tools.java.ClassFile;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
public class CompilerController {

    @Autowired
    private CompilerService compilerService;

    @Autowired
    private TemporaryFileManager tempFileManager;

    @RequestMapping("/compile")
    public List<String> compileSources(@RequestBody String sourceCode) throws FileManagerException, CompilationException {
        Path pathToSourceCodeFile = tempFileManager.getFileWithContents(sourceCode);

        // NOTE: Should this SourceFile be a public thing?
        List<ClassFile> classFiles = compilerService.compile(Collections.singletonList(new SourceFile(pathToSourceCodeFile)));
        return classFiles.stream()
                .map(classFile -> classFile.getName())
                .collect(toList());
    }
}

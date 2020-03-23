package com.github.thepalbi.SootLab.service.controllers;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.thepalbi.SootLab.service.services.ClassFile;
import com.github.thepalbi.SootLab.service.services.CompilerService;
import com.github.thepalbi.SootLab.service.services.SourceFile;
import com.github.thepalbi.SootLab.service.services.TemporaryFileManager;
import com.github.thepalbi.SootLab.service.services.erros.CompilationException;
import com.github.thepalbi.SootLab.service.services.erros.FileManagerException;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@RestController
public class CompilerController {

    @Autowired
    private CompilerService compilerService;

    @Autowired
    private TemporaryFileManager tempFileManager;

    @RequestMapping("/compile")
    public List<String> compileSources(@RequestBody String sourceCode) throws FileManagerException, CompilationException {

        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);
        Optional<ClassOrInterfaceDeclaration> classDeclaration = compilationUnit.findAll(ClassOrInterfaceDeclaration.class).stream()
                .findFirst();

        if (!classDeclaration.isPresent()) {
            // TODO: Fail here, this is DEV
            return EMPTY_LIST;
        }

        Path pathToSourceCodeFile = tempFileManager.getFileWithContentsNamed(sourceCode, classDeclaration.get().getNameAsString() + ".java");

        // NOTE: Should this SourceFile be a public thing?
        List<ClassFile> classFiles = compilerService.compile(singletonList(new SourceFile(pathToSourceCodeFile)));
        return classFiles.stream()
                .map(classFile -> classFile.getPath())
                .collect(toList());
    }
}

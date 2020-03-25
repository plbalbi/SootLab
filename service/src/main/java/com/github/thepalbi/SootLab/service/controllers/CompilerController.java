package com.github.thepalbi.SootLab.service.controllers;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.thepalbi.SootLab.service.compilation.CompilationResult;
import com.github.thepalbi.SootLab.service.services.*;
import com.github.thepalbi.SootLab.service.services.erros.CompilationException;
import com.github.thepalbi.SootLab.service.services.erros.FileManagerException;
import com.github.thepalbi.SootLab.service.services.erros.PackagerException;
import com.github.thepalbi.SootLab.service.services.erros.SootException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import static java.util.Collections.singletonList;

@RestController
@CrossOrigin("http://localhost:8000")
public class CompilerController {

    private Logger logger = LoggerFactory.getLogger(CompilerController.class);

    @Autowired
    private CompilerService compilerService;

    @Autowired
    private TemporaryFileManager tempFileManager;

    @Autowired
    private PackagerService packagerService;

    @Autowired
    private SootService sootService;

    @PostMapping("/compile")
    public CompilationResult compileSources(@RequestBody String sourceCode) throws FileManagerException, CompilationException, PackagerException, SootException {
        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);
        Optional<ClassOrInterfaceDeclaration> classDeclaration = compilationUnit.findAll(ClassOrInterfaceDeclaration.class).stream()
                .findFirst();

        if (!classDeclaration.isPresent()) {
            // TODO: Fail here, this is DEV
            return new CompilationResult("ERROR");
        }

        // TODO: This should utilize a directory per session, or use the UUID postfixed file solution
        Path pathToSourceCodeFile = tempFileManager.getFileWithContentsNamed(compilationUnit.toString(), classDeclaration.get().getNameAsString() + ".java");

        // NOTE: Should this SourceFile be a public thing?
        // NOTE: Maybe return CompilationResult or sth like that?
        File compiledClassesDirectory = compilerService.compile(singletonList(new SourceFile(pathToSourceCodeFile)));
        Path jarPackagedClasses = packagerService.pack(compiledClassesDirectory);
        String jimpleGeneratedSource = sootService.runClassThroughBodyPack(jarPackagedClasses, classDeclaration.get().getFullyQualifiedName().get());
        return new CompilationResult(jimpleGeneratedSource);
    }
}

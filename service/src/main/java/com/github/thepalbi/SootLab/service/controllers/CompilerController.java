package com.github.thepalbi.SootLab.service.controllers;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.thepalbi.SootLab.service.services.CompilerService;
import com.github.thepalbi.SootLab.service.services.PackagerService;
import com.github.thepalbi.SootLab.service.services.SourceFile;
import com.github.thepalbi.SootLab.service.services.TemporaryFileManager;
import com.github.thepalbi.SootLab.service.services.erros.CompilationException;
import com.github.thepalbi.SootLab.service.services.erros.FileManagerException;
import com.github.thepalbi.SootLab.service.services.erros.PackagerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soot.PackManager;
import soot.Printer;
import soot.Scene;
import soot.options.Options;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Optional;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;

@RestController
public class CompilerController {

    @Autowired
    private CompilerService compilerService;

    @Autowired
    private TemporaryFileManager tempFileManager;

    @Autowired
    private PackagerService packagerService;

    @RequestMapping("/compile")
    public String compileSources(@RequestBody String sourceCode) throws FileManagerException, CompilationException, PackagerException {

        CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);
        Optional<ClassOrInterfaceDeclaration> classDeclaration = compilationUnit.findAll(ClassOrInterfaceDeclaration.class).stream()
                .findFirst();

        if (!classDeclaration.isPresent()) {
            // TODO: Fail here, this is DEV
            return "ERROR";
        }

        Path pathToSourceCodeFile = tempFileManager.getFileWithContentsNamed(sourceCode, classDeclaration.get().getNameAsString() + ".java");

        // NOTE: Should this SourceFile be a public thing?

        // NOTE: Maybe return CompilationResult or sth like that?
        File compiledClassesDirectory = compilerService.compile(singletonList(new SourceFile(pathToSourceCodeFile)));

        Path jarPackagedClasses = packagerService.pack(compiledClassesDirectory);

        // Jimple output format
        Options.v().set_output_format(Options.output_format_jimple);
        // Add rt.jar
        Options.v().set_prepend_classpath(true);
        // Allow phantom refs
        Options.v().set_allow_phantom_refs(true);
        // FIXME: This only takes or a directory, or a path to JAR.
        // For this case I think it would be appropriate to package the session into a jar, and supply that to Soot.
        // Add path to class files to classPath
        Options.v().set_process_dir(singletonList(jarPackagedClasses.toString()));

        // FIXME: Maybe this can be moved to just run once, and then run the pack every time is needed.
        // Or at least just re-load necessary classes.
        Scene.v().loadNecessaryClasses();

        PackManager.v().runBodyPacks();

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        Printer.v().printTo(Scene.v().getSootClass("test.A"), printWriter);
        return stringWriter.toString();
    }
}

package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.compilation.CompilerDiagnostic;
import com.github.thepalbi.SootLab.service.domain.SourceFile;
import com.github.thepalbi.SootLab.service.services.erros.CompilationException;
import com.github.thepalbi.SootLab.service.services.erros.FileManagerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@Service
public class JavacCompilerService implements CompilerService {

    // TODO: Change this to use Janino. There are some problem when using SystemJavaCompiler with
    // custom classLoaders as SpringBoot uses. http://janino-compiler.github.io/janino/

    public static final String PACKAGE_TO_LIST_FROM = "";
    private TemporaryFileManager tempFileManager;
    private JavaCompiler compiler;
    private StandardJavaFileManager fileManager;

    @Autowired
    public JavacCompilerService(TemporaryFileManager tempFileManager) {
        compiler = requireNonNull(ToolProvider.getSystemJavaCompiler(), "Compiler instance cannot be null");
        fileManager = compiler.getStandardFileManager(null, null, null);
        this.tempFileManager = tempFileManager;

    }

    /**
     * @param sources
     * @return The File to the directory containing the compiled outputs
     * @throws CompilationException
     * @throws FileManagerException
     */
    @Override
    public File compile(List<SourceFile> sources) throws CompilationException, FileManagerException {
        File compiledClassesDirectory = tempFileManager.getDirectory().toFile();
        try {
            // Creating a directory for each compilation
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(compiledClassesDirectory));
        } catch (IOException e) {
            // TODO: Handle IOException better
            throw new FileManagerException(e);
        }

        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        JavaCompiler.CompilationTask task = compiler.getTask(null,
                fileManager,
                diagnosticCollector,
                null,
                null,
                sources);

        if (!task.call()) {
            // Here, for further error-tooling, getStartPosition, getEndPosition can be used.
            List<CompilerDiagnostic> collectedDiagnostics = diagnosticCollector.getDiagnostics().stream()
                    .map(diagnostic -> new CompilerDiagnostic(
                            diagnostic.getCode(),
                            diagnostic.getMessage(Locale.ENGLISH),
                            diagnostic.getKind(),
                            Collections.emptyList(),
                            diagnostic.getLineNumber(),
                            diagnostic.getColumnNumber()))
                    .collect(toList());
            throw new CompilationException(collectedDiagnostics);
        }
        return compiledClassesDirectory;
    }

    @PreDestroy
    public void cleanCompilerTools() throws IOException {
        fileManager.close();
    }
}

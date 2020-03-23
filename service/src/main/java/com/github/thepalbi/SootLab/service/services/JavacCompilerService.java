package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.services.erros.CompilationException;
import com.github.thepalbi.SootLab.service.services.erros.FileManagerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@Service
public class JavacCompilerService implements CompilerService {

    public static final String PACKAGE_TO_LIST_FROM = "";
    private TemporaryFileManager tempFileManager;
    private JavaCompiler compiler;
    private StandardJavaFileManager fileManager;

    @Autowired
    public JavacCompilerService(TemporaryFileManager tempFileManager) throws FileManagerException, IOException {
        compiler = ToolProvider.getSystemJavaCompiler();
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
            // TODO: Extract this into a custom DiagnosticCollector
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Compilation diagnostics: \n");
            diagnosticCollector.getDiagnostics().stream()
                    .forEach(diagnostic ->
                            messageBuilder.append(String.format(
                                    "[%s] %s-%d: %s",
                                    diagnostic.getKind().toString(),
                                    diagnostic.getSource().getName(),
                                    diagnostic.getLineNumber(),
                                    diagnostic.getMessage(Locale.ENGLISH)))
                                    .append("\n"));
            throw new CompilationException(messageBuilder.toString());
        }
        return compiledClassesDirectory;
    }

    @PreDestroy
    public void cleanCompilerTools() throws IOException {
        fileManager.close();
    }
}

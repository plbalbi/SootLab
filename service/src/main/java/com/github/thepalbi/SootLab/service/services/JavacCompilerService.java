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

    private JavaCompiler compiler;
    private StandardJavaFileManager fileManager;

    private final TemporaryFileManager tempFileManager;

    @Autowired
    public JavacCompilerService(TemporaryFileManager tempFileManager) throws FileManagerException, IOException {
        this.tempFileManager = tempFileManager;
        compiler = ToolProvider.getSystemJavaCompiler();
        fileManager = compiler.getStandardFileManager(null, null, null);

        // TODO: Handle IOException better
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(tempFileManager.getDirectory().toFile()));
    }

    @Override
    public List<ClassFile> compile(List<SourceFile> sources) throws CompilationException {
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        JavaCompiler.CompilationTask task = compiler.getTask(null,
                fileManager,
                diagnosticCollector,
                null,
                null,
                sources);
        if (!task.call()) {
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
        try {
            Iterable<JavaFileObject> compiledUnits = fileManager.list(StandardLocation.CLASS_OUTPUT,
                    "", Collections.singleton(JavaFileObject.Kind.CLASS), true);
            List<ClassFile> compilerClassFiles = new LinkedList<>();
            compiledUnits.forEach(compiledUnit -> compilerClassFiles.add(new ClassFile(compiledUnit.toUri())));
            return compilerClassFiles;
        } catch (IOException e) {
            throw new CompilationException("Error listing compiled sources");
        }
    }

    @PreDestroy
    public void cleanCompilerTools() throws IOException {
        fileManager.close();
    }
}

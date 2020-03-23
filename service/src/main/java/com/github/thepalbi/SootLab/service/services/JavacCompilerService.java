package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.services.erros.CompilationException;
import com.github.thepalbi.SootLab.service.services.erros.FileManagerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.jvm.hotspot.debugger.windbg.WindbgThreadFactory;
import sun.tools.java.ClassFile;

import javax.annotation.PreDestroy;
import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, sources);
        if (!task.call()){
            throw new CompilationException();
        }
        try {
            Iterable<JavaFileObject> compiledUnits = fileManager.list(StandardLocation.CLASS_OUTPUT,
                    "", Collections.singleton(JavaFileObject.Kind.CLASS), true);
            List<ClassFile> compilerClassFiles = new LinkedList<>();
            compiledUnits.forEach(compiledUnit -> compilerClassFiles.add(new ClassFile(new File(compiledUnit.toUri()))));
            return compilerClassFiles;
        } catch (IOException e) {
            throw new CompilationException();
        }
    }

    @PreDestroy
    public void cleanCompilerTools() throws IOException {
        fileManager.close();
    }
}

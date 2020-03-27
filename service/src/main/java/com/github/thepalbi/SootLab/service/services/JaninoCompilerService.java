package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.compilation.CompilerDiagnostic;
import com.github.thepalbi.SootLab.service.domain.SootCompileRequest;
import com.github.thepalbi.SootLab.service.services.erros.CompilationException;
import com.github.thepalbi.SootLab.service.services.erros.FileManagerException;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.ICompiler;
import org.codehaus.commons.compiler.InternalCompilerException;
import org.codehaus.commons.compiler.util.resource.DirectoryResourceCreator;
import org.codehaus.commons.compiler.util.resource.Resource;
import org.codehaus.commons.compiler.util.resource.StringResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.tools.Diagnostic;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Service
public class JaninoCompilerService implements CompilerService {
    public static final String COMPILE_ERROR = "COMPILE_ERROR";

    // TODO: Change this to use Janino. There are some problem when using SystemJavaCompiler with
    // custom classLoaders as SpringBoot uses. http://janino-compiler.github.io/janino/

    private TemporaryFileManager tempFileManager;
    private ICompiler compiler;

    @Autowired
    public JaninoCompilerService(TemporaryFileManager tempFileManager) throws Exception {
        compiler = requireNonNull(CompilerFactoryFactory.getDefaultCompilerFactory().newCompiler(), "Compiler instance cannot be null");
        this.tempFileManager = tempFileManager;

    }

    @Override
    public File compile(SootCompileRequest request) throws CompilationException, FileManagerException {

        // TODO: This ResourceCreator can be changed to an in-memory one
        File destinationDirectory = tempFileManager.getDirectory().toFile();
        compiler.setClassFileCreator(new DirectoryResourceCreator(destinationDirectory));
        List<CompilerDiagnostic> collectedErrors = new LinkedList<>();
        compiler.setCompileErrorHandler((message, optionalLocation) -> collectedErrors.add(new CompilerDiagnostic(
                COMPILE_ERROR,
                message,
                Diagnostic.Kind.ERROR,
                Collections.emptyList(),
                optionalLocation.getLineNumber(),
                optionalLocation.getColumnNumber()
        )));
        try {
            compiler.compile(new Resource[]{new StringResource(request.getFullyQualifiedName().replace(".", "/") + ".java", request.getSourceCode())});
        } catch (CompileException | InternalCompilerException exception) {
            throw new CompilationException(collectedErrors);
        } catch (Exception e) {
            throw new RuntimeException("Unhandled error in compile stage", e);
        }

        return destinationDirectory;
    }
}

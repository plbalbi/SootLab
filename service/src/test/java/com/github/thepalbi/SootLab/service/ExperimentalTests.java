package com.github.thepalbi.SootLab.service;

import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class ExperimentalTests {

    private JavaCompiler compiler;
    private StandardJavaFileManager fileManager;
    private Path tempDirectory;

    @BeforeEach
    public void initializeCompilationTools() throws IOException {
        compiler = ToolProvider.getSystemJavaCompiler();
        fileManager = compiler.getStandardFileManager(null, null, null);
        tempDirectory = Files.createTempDirectory("compilation-test-");
    }

    @AfterEach
    public void closeFileManager() throws IOException {
        fileManager.close();
    }

    @Test
    public void test() throws IOException {
        // https://stackoverflow.com/questions/39239285/how-to-get-list-of-class-files-generated-by-javacompiler-compilationtask
        File[] filesToCompile = new File[] {new File("/Users/thepalbi/Facultad/aap/SootLab/service/src/test/java/com/github/thepalbi/SootLab/service/SampleThingForCompilation.java")};
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(tempDirectory.toFile()));
        Iterable<? extends JavaFileObject> outputObjects = fileManager.getJavaFileObjects(filesToCompile);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, outputObjects);
        if (task.call()) {
            Iterable<JavaFileObject> compiledUnits = fileManager.list(StandardLocation.CLASS_OUTPUT,
                    "", Collections.singleton(JavaFileObject.Kind.CLASS), true);
            List<JavaFileObject> compiledUnitsList = new LinkedList<>();
            compiledUnits.forEach(compiledUnit -> compiledUnitsList.add(compiledUnit));

            assertThat(compiledUnitsList).hasSize(1);
        } else {
            fail();
        }
        fileManager.close();
    }
}

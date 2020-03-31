package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.services.erros.SootException;
import org.springframework.stereotype.Service;
import soot.G;
import soot.PackManager;
import soot.Printer;
import soot.Scene;
import soot.options.Options;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

import static java.util.Collections.singletonList;

/**
 * Soot service implementation that uses the Jimple IR.
 */
@Service
public class JimpleSootService implements SootService {

    // NOTE: Used synchronized for the moment. It's a horrible solution.
    // TODO: Implement some kind worker-pool to process Soot service requests, by having one classloader per worker, with Soot loaded.

    @Override
    public synchronized String runClassThroughBodyPack(Path pathToJar, String className) throws SootException {
        // Jimple output format
        Options.v().set_output_format(Options.output_format_jimple);
        // Add rt.jar
        Options.v().set_prepend_classpath(true);
        // Allow phantom refs
        Options.v().set_allow_phantom_refs(true);
        // For this case I think it would be appropriate to package the session into a jar, and supply that to Soot.
        // Add path to class files to classPath
        Options.v().set_process_dir(singletonList(pathToJar.toString()));
        try {
            // NOTE: Maybe this can be moved to just run once, and then run the pack every time is needed.
            // Or at least just re-load necessary classes.
            Scene.v().loadNecessaryClasses();
            PackManager.v().runBodyPacks();
            StringWriter stringWriter = new StringWriter();
            Printer.v().printTo(Scene.v().getSootClass(className), new PrintWriter(stringWriter));
            return stringWriter.toString();
        } catch (Exception e) {
            throw new SootException(e);
        } finally {
            // Reset the whole Soot environment
            G.reset();
        }
    }
}

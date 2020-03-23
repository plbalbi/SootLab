package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.services.erros.SootException;

import java.nio.file.Path;

/**
 * Service for running Soot.
 */
public interface SootService {
    /**
     * Set's the running class as target of Soot, loads it apart from the necessary classes,
     * and runs the body transformation pack for the implemented IR.
     *
     * @param pathToJar Path to JAR file containing target class.
     * @param className Fully qualified name of the target class.
     * @return The Soot IR converted class as a String.
     */
    String runClassThroughBodyPack(Path pathToJar, String className) throws SootException;
}

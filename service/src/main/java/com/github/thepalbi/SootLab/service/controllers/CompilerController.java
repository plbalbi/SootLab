package com.github.thepalbi.SootLab.service.controllers;

import com.github.thepalbi.SootLab.service.compilation.CompilationResult;
import com.github.thepalbi.SootLab.service.domain.SootCompileRequest;
import com.github.thepalbi.SootLab.service.services.*;
import com.github.thepalbi.SootLab.service.services.erros.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Path;

@RestController
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

    @Autowired
    private EnrichmentService enrichmentService;

    @PostMapping("/compile")
    public CompilationResult compileAndTranslate(@RequestBody String sourceCode) throws FileManagerException, CompilationException, PackagerException, SootException, ParseException {
        SootCompileRequest baseRequest = SootCompileRequest.withSourceCode(sourceCode);
        SootCompileRequest withClassEnrichedRequest = enrichmentService.enrich(baseRequest);

        // If no error is thrown by the enriched, the request contains the target class name.
        File compiledClassesDirectory = compilerService.compile(withClassEnrichedRequest);
        Path jarPackagedClasses = packagerService.pack(compiledClassesDirectory);
        String jimpleGeneratedSource = sootService.runClassThroughBodyPack(jarPackagedClasses, withClassEnrichedRequest.getFullyQualifiedName());
        return new CompilationResult(jimpleGeneratedSource);
    }
}

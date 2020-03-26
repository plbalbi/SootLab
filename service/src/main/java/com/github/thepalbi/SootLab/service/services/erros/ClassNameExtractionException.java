package com.github.thepalbi.SootLab.service.services.erros;

import com.github.thepalbi.SootLab.service.compilation.CompilerDiagnostic;

import java.util.List;

public class ClassNameExtractionException extends ParseException {
    public ClassNameExtractionException(List<CompilerDiagnostic> diagnostics) {
        super(diagnostics);
    }
}

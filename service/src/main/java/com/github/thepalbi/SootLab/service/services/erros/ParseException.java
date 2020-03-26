package com.github.thepalbi.SootLab.service.services.erros;

import com.github.thepalbi.SootLab.service.compilation.CompilerDiagnostic;

import java.util.List;

public class ParseException extends DiagnosticsDrivenException {
    public ParseException(List<CompilerDiagnostic> diagnostics) {
        super(diagnostics);
    }
}

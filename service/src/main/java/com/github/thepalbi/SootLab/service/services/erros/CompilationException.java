package com.github.thepalbi.SootLab.service.services.erros;

import com.github.thepalbi.SootLab.service.compilation.CompilerDiagnostic;

import java.util.List;

public class CompilationException extends Exception {

    private List<CompilerDiagnostic> diagnostics;

    public CompilationException(List<CompilerDiagnostic> diagnostics) {
        this.diagnostics = diagnostics;
    }

    public List<CompilerDiagnostic> getDiagnostics() {
        return diagnostics;
    }
}

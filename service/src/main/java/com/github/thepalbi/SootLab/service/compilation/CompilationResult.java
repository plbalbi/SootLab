package com.github.thepalbi.SootLab.service.compilation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class CompilationResult {

    @JsonProperty("x")
    private String result;

    @JsonProperty("diagnostics")
    private List<CompilerDiagnostic> diagnostics;

    public CompilationResult(String result) {
        this.result = result;
        this.diagnostics = Collections.emptyList();
    }

    public CompilationResult(String result, List<CompilerDiagnostic> diagnostics) {
        this.result = result;
        this.diagnostics = diagnostics;
    }
}

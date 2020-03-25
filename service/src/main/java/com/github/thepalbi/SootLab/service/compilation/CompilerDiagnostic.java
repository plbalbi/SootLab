package com.github.thepalbi.SootLab.service.compilation;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.tools.Diagnostic;
import java.util.List;

public class CompilerDiagnostic {
    @JsonProperty("id")
    private String id;

    @JsonProperty("message")
    private String message;

    @JsonProperty("severity")
    private Diagnostic.Kind severity;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("span")
    private Span span;

    public CompilerDiagnostic(String id, String message, Diagnostic.Kind severity, List<String> tags, long lineNumber, long colNumber) {
        this.id = id;
        this.message = message;
        this.severity = severity;
        this.tags = tags;
        this.span = new Span(lineNumber, colNumber);
    }

    public boolean isOfKind(Diagnostic.Kind anotherKind) {
        return this.severity.equals(anotherKind);
    }

    @Override
    public String toString() {
        return String.format("error %s: %s", this.id, this.message);
    }

    private class Span {
        @JsonProperty("line_number")
        private final long lineNumber;

        @JsonProperty("col_number")
        private final long colNumber;

        public Span(long lineNumber, long colNumber) {
            this.lineNumber = lineNumber;
            this.colNumber = colNumber;
        }
    }
}

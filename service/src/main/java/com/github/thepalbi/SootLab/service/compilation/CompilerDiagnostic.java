package com.github.thepalbi.SootLab.service.compilation;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.tools.Diagnostic;
import java.util.List;
import java.util.Optional;

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
    private Optional<Span> span = Optional.empty();

    public CompilerDiagnostic(String id, String message, Diagnostic.Kind severity, List<String> tags, long lineNumber, long colNumber) {
        this(id, message, severity, tags);
        this.span = Optional.of(new Span(lineNumber, colNumber));
    }

    public CompilerDiagnostic(String id, String message, Diagnostic.Kind severity, List<String> tags) {
        this.id = id;
        this.message = message;
        this.severity = severity;
        this.tags = tags;
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

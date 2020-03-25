package com.github.thepalbi.SootLab.service.services.erros;

import com.github.thepalbi.SootLab.service.compilation.CompilationResult;
import com.github.thepalbi.SootLab.service.compilation.CompilerDiagnostic;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.tools.Diagnostic;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CompilationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CompilationException.class)
    protected ResponseEntity<Object> handleJavacCompilationError(CompilationException exception) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        exception.getDiagnostics().stream()
                .filter(diagnostic -> diagnostic.isOfKind(Diagnostic.Kind.ERROR))
                .forEach(errorDiagnostic -> errorMessageBuilder.append(errorDiagnostic.toString()).append("\n"));
        CompilationResult result = new CompilationResult(errorMessageBuilder.toString(), exception.getDiagnostics());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}

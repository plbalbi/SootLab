package com.github.thepalbi.SootLab.service.services;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.thepalbi.SootLab.service.compilation.CompilerDiagnostic;
import com.github.thepalbi.SootLab.service.domain.SootCompileRequest;
import com.github.thepalbi.SootLab.service.services.erros.ClassNameExtractionException;
import com.github.thepalbi.SootLab.service.services.erros.ParseException;
import org.springframework.stereotype.Service;

import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class ClassNameExtractorService implements EnrichmentService {

    public static final String PARSE_ERROR = "PARSE_ERROR";
    public static final List<CompilerDiagnostic> NO_CLASS_DIAGNOSTIC_LIST = Collections.singletonList(new CompilerDiagnostic(
            PARSE_ERROR,
            "No class name found, or class somehow does not have fully qualified name.",
            Diagnostic.Kind.ERROR,
            Collections.emptyList()
    ));

    @Override
    public SootCompileRequest enrich(SootCompileRequest request) throws ParseException {
        CompilationUnit compilationUnit;
        try {
            compilationUnit = StaticJavaParser.parse(request.getSourceCode());
        } catch (ParseProblemException parseException) {
            List<CompilerDiagnostic> diagnosticList = parseException.getProblems().stream()
                    .map(problem -> new CompilerDiagnostic(
                            PARSE_ERROR,
                            problem.getVerboseMessage(),
                            Diagnostic.Kind.ERROR,
                            Collections.emptyList()
                    ))
                    .collect(toList());
            throw new ParseException(diagnosticList);
        }
        Optional<ClassOrInterfaceDeclaration> classDeclaration = compilationUnit
                .findAll(ClassOrInterfaceDeclaration.class).stream()
                .findFirst();
        if (!classDeclaration.isPresent() || !classDeclaration.get().getFullyQualifiedName().isPresent()) {
            throw new ClassNameExtractionException(NO_CLASS_DIAGNOSTIC_LIST);
        }

        SootCompileRequest sootCompileRequest = new SootCompileRequest();
        sootCompileRequest.setSourceCode(request.getSourceCode());
        sootCompileRequest.setMainClassName(classDeclaration.get().getNameAsString());
        sootCompileRequest.setFullyQualifiedName(classDeclaration.get().getFullyQualifiedName().get());
        return sootCompileRequest;
    }
}

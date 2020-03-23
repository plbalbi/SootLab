package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.services.erros.CompilationException;
import sun.tools.java.ClassFile;

import java.util.List;

public interface CompilerService {
    List<ClassFile> compile(List<SourceFile> sources) throws CompilationException;
}
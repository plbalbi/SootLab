package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.services.erros.CompilationException;
import com.github.thepalbi.SootLab.service.services.erros.FileManagerException;

import java.io.File;
import java.util.List;

public interface CompilerService {
    File compile(List<SourceFile> sources) throws CompilationException, FileManagerException;
}

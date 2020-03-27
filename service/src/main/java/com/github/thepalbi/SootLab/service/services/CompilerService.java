package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.domain.SootCompileRequest;
import com.github.thepalbi.SootLab.service.services.erros.CompilationException;
import com.github.thepalbi.SootLab.service.services.erros.FileManagerException;

import java.io.File;

public interface CompilerService {
    File compile(SootCompileRequest request) throws CompilationException, FileManagerException;
}

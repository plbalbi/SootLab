package com.github.thepalbi.SootLab.service.services;

import com.github.thepalbi.SootLab.service.domain.SootCompileRequest;
import com.github.thepalbi.SootLab.service.services.erros.ParseException;

/**
 * Enriches some input source code with additional meta data.
 */
public interface EnrichmentService {
    /**
     * Enriches the input source code.
     *
     * @param sourceCode The input source code.
     * @return The enriched data.
     * @throws ParseException if an error is raised while processing the enrichment operation.
     */
    SootCompileRequest enrich(SootCompileRequest sourceCode) throws ParseException;
}

package com.github.thepalbi.SootLab.service.domain;

public class SootCompileRequest {
    private String sourceCode;
    private String mainClassName;

    public static SootCompileRequest withSourceCode(String sourceCode) {
        SootCompileRequest newRequest = new SootCompileRequest();
        newRequest.setSourceCode(sourceCode);
        return newRequest;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public void setMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }
}

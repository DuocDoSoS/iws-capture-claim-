package com.rstn.iws.webservice.model;

public class DocumentPropertyModel {
    String documentType;
    String submissionProcess;
    String docSource;

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getSubmissionProcess() {
        return submissionProcess;
    }

    public void setSubmissionProcess(String submissionProcess) {
        this.submissionProcess = submissionProcess;
    }

    public String getDocSource() {
        return docSource;
    }

    public void setDocSource(String docSource) {
        this.docSource = docSource;
    }
}

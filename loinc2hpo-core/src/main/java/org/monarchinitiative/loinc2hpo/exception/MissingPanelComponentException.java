package org.monarchinitiative.loinc2hpo.exception;

public class MissingPanelComponentException extends Exception{
    public MissingPanelComponentException() {
        super();
    }
    public MissingPanelComponentException(String msg) {
        super(msg);
    }
}

package org.monarchinitiative.loinc2hpo.exception;

public class UnrecognizedLoincCodeException extends Exception {
    public UnrecognizedLoincCodeException() {
        super();
    }

    public UnrecognizedLoincCodeException(String msg) {
        super(msg);
    }
}

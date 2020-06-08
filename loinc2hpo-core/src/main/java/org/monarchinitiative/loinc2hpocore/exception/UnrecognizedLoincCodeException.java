package org.monarchinitiative.loinc2hpocore.exception;

public class UnrecognizedLoincCodeException extends Exception {
    public UnrecognizedLoincCodeException() {
        super();
    }

    public UnrecognizedLoincCodeException(String msg) {
        super(msg);
    }
}

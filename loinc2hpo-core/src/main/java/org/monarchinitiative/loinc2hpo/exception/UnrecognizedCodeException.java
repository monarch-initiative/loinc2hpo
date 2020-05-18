package org.monarchinitiative.loinc2hpo.exception;

public class UnrecognizedCodeException extends Loinc2HpoException {

    public UnrecognizedCodeException() {
        super();
    }
    public UnrecognizedCodeException(String msg) {
        super(msg);
    }
}

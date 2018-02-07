package org.monarchinitiative.loinc2hpo.exception;

public class ReferenceNotFoundException extends Loinc2HpoException{

    public ReferenceNotFoundException() {
        super();
    }
    public ReferenceNotFoundException(String msg) {
        super(msg);
    }
}

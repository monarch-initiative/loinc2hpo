package org.monarchinitiative.loinc2hpo.exception;

public class AmbiguousReferenceException extends Loinc2HpoException{
    public AmbiguousReferenceException() {
        super();
    }
    public AmbiguousReferenceException(String msg) {
        super(msg);
    }
}

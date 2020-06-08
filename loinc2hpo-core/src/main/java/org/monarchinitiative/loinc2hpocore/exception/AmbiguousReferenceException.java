package org.monarchinitiative.loinc2hpocore.exception;

public class AmbiguousReferenceException extends Loinc2HpoException{
    public AmbiguousReferenceException() {
        super();
    }
    public AmbiguousReferenceException(String msg) {
        super(msg);
    }
}

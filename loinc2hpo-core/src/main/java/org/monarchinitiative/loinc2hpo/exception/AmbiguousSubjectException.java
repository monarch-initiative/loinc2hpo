package org.monarchinitiative.loinc2hpo.exception;

public class AmbiguousSubjectException extends Loinc2HpoException {

    public AmbiguousSubjectException() {
        super();
    }
    public AmbiguousSubjectException(String msg) {
        super(msg);
    }
}

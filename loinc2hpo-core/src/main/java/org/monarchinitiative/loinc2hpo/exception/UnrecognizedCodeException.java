package org.monarchinitiative.loinc2hpo.exception;

import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;

public class UnrecognizedCodeException extends Loinc2HpoException {

    public UnrecognizedCodeException() {
        super();
    }
    public UnrecognizedCodeException(String msg) {
        super(msg);
    }
}

package org.monarchinitiative.loinc2hpo.exception;

import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotation;

public class LoincCodeNotAnnotatedException extends Loinc2HpoException{

    public LoincCodeNotAnnotatedException () {

    }

    public LoincCodeNotAnnotatedException (String msg) {

        super(msg);

    }
}

package org.monarchinitiative.loinc2hpo.exception;

public class LoincCodeNotAnnotatedException extends Loinc2HpoException{

    public LoincCodeNotAnnotatedException () {

    }

    public LoincCodeNotAnnotatedException (String msg) {

        super(msg);

    }
}

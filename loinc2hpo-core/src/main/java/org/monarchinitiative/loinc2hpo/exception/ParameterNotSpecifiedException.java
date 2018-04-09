package org.monarchinitiative.loinc2hpo.exception;

import java.text.ParseException;

public class ParameterNotSpecifiedException extends Exception{

    public ParameterNotSpecifiedException() {

    }

    public ParameterNotSpecifiedException(String msg) {
        super(msg);
    }
}

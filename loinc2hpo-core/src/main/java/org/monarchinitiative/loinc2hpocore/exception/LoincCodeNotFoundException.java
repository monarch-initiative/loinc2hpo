package org.monarchinitiative.loinc2hpocore.exception;

public class LoincCodeNotFoundException extends ObservationFieldNotFoundException {

    public LoincCodeNotFoundException() {
        super();
    }
    public LoincCodeNotFoundException(String msg) {
        super(msg);
    }
}

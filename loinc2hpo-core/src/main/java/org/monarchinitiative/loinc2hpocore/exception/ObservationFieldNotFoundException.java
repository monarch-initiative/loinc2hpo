package org.monarchinitiative.loinc2hpocore.exception;

public class ObservationFieldNotFoundException extends Exception {
    public ObservationFieldNotFoundException() {
        super();
    }
    public ObservationFieldNotFoundException(String msg){
        super(msg);
    }
}

package org.monarchinitiative.loinc2hpo.exception;

public class ObservationFieldNotFoundException extends Exception {
    public ObservationFieldNotFoundException() {
        super();
    }
    public ObservationFieldNotFoundException(String msg){
        super(msg);
    }
}

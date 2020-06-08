package org.monarchinitiative.loinc2hpocore.exception;

public class AmbiguousResultsFoundException extends Loinc2HpoException {
    public AmbiguousResultsFoundException(){
        super();
    }
    public AmbiguousResultsFoundException(String msg){
        super(msg);
    }
}

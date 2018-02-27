package org.monarchinitiative.loinc2hpo.exception;

public class InternalCodeNotFoundException extends Exception{
    public InternalCodeNotFoundException(){
        super();
    }
    public InternalCodeNotFoundException(String msg){
        super(msg);
    }
}

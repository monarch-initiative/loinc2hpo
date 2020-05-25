package org.monarchinitiative.loinc2hpocore.exception;

public class InternalCodeNotFoundException extends Exception{
    public InternalCodeNotFoundException(){
        super();
    }
    public InternalCodeNotFoundException(String msg){
        super(msg);
    }
}

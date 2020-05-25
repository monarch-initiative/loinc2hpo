package org.monarchinitiative.loinc2hpocore.exception;

public class AnnotationNotFoundException extends Loinc2HpoException {
    public AnnotationNotFoundException(){
        super();
    }
    public AnnotationNotFoundException(String msg){
        super(msg);
    }
}

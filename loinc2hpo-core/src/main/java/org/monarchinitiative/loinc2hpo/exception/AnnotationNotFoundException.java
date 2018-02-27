package org.monarchinitiative.loinc2hpo.exception;

public class AnnotationNotFoundException extends Loinc2HpoException {
    public AnnotationNotFoundException(){
        super();
    }
    public AnnotationNotFoundException(String msg){
        super(msg);
    }
}

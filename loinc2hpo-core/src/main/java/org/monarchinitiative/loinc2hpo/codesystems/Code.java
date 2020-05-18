package org.monarchinitiative.loinc2hpo.codesystems;

import org.hl7.fhir.dstu3.model.Coding;

import java.io.Serializable;

/**
 * This is an class for coded values. This correspond to the Coding class in hapi-fhir with some modification (equal method)
 */
public class Code implements Serializable {
    private static final long serialVersionUID = 1L;
    private String system;
    private String code;
    private String display;


    public Code(){

    }


    public Code(String system, String code, String display){
        this.system = system;
        this.code = code;
        this.display = display;
    }

    public Code(Code otherCode) {
        this.system = otherCode.system;
        this.code = otherCode.code;
        this.display = otherCode.display;
    }

    public Code (Coding coding) {
        this.system = coding.getSystem();
        this.code = coding.getCode();
    }

    public static Code getNewCode(){
        return new Code();
    }
    public String getSystem() {
        return system;
    }

   public Code setSystem(String system) {
        this.system = system;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Code setCode(String code) {
        this.code = code;
        return this;
    }

    public String getDisplay() {
        return display;
    }

    public Code setDisplay(String display) {
        this.display = display;
        return this;
    }


    @Override
    public boolean equals(Object obj){
        if (obj instanceof Code) {
            Code other = (Code) obj;
            return other.getSystem().equals(this.system) && other.getCode().equals(this.code);
        }
        return false;
    }
    @Override
    public int hashCode(){
        return system.hashCode() + code.hashCode()*37;

    }

    @Override
    public String toString(){

        String toString = String.format("System: %s; Code: %s, Display: %s", system, code, display);
        return toString;

    }
}

package org.monarchinitiative.loinc2hpocore.codesystems;

import org.hl7.fhir.dstu3.model.Coding;

import java.io.Serializable;
import java.util.Objects;

/**
 * This is an class for coded values. This corresponds to the Coding class in hapi-fhir with some
 * modification (equal method)
 */
public class Code implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DISPLAY_NOT_AVAILABLE = "n/a";
    private final String system;
    private final String code;
    private final String display;


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

    public Code(Coding coding) {
        this.system = coding.getSystem();
        this.code = coding.getCode();
        this.display = DISPLAY_NOT_AVAILABLE;
    }


    public String getSystem() {
        return system;
    }

    public String getCode() {
        return code;
    }

    public String getDisplay() {
        return display;
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
        return Objects.hash(this.system, this.code);
    }

    @Override
    public String toString(){
        return String.format("System: %s; Code: %s, Display: %s", system, code, display);
    }

    public static Code fromSystemAndCode(String system, String code) {
        return new Code(system, code, DISPLAY_NOT_AVAILABLE);
    }

    public static Code fromSystemCodeAndDisplay(String system, String code, String display) {
        return new Code(system, code, display);
    }
}

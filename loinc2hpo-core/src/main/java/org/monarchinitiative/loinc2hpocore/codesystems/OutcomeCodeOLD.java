package org.monarchinitiative.loinc2hpocore.codesystems;

//import org.hl7.fhir.dstu3.model.Coding;

import java.util.Objects;

/**
 * This is an class for coded values. This corresponds to the Coding class in hapi-fhir with some
 * modification (equal method)
 */
public class OutcomeCodeOLD {
    private static final String DISPLAY_NOT_AVAILABLE = "n/a";

    private final String code;
    private final String display;


    public OutcomeCodeOLD(String code, String display){
        this.code = code;
        this.display = display;
    }

    public OutcomeCodeOLD(OutcomeCodeOLD otherCode) {
        this.code = otherCode.code;
        this.display = otherCode.display;
    }


    public String getCode() {
        return code;
    }

    public String getDisplay() {
        return display;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof OutcomeCodeOLD) {
            OutcomeCodeOLD other = (OutcomeCodeOLD) obj;
            return other.getCode().equals(this.code);
        }
        return false;
    }
    @Override
    public int hashCode(){
        return Objects.hash(this.code);
    }

    @Override
    public String toString(){
        return String.format("Code: %s, Display: %s", code, display);
    }

    public static OutcomeCodeOLD fromSystemAndCode(String code) {
        return new OutcomeCodeOLD(code, DISPLAY_NOT_AVAILABLE);
    }

    public static OutcomeCodeOLD fromSystemCodeAndDisplay(String code, String display) {
        return new OutcomeCodeOLD(code, display);
    }
}

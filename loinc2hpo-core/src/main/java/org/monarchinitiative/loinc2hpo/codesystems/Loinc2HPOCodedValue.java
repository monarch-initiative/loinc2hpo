package org.monarchinitiative.loinc2hpo.codesystems;

import org.monarchinitiative.loinc2hpo.exception.UnrecognizedCodeException;

public enum Loinc2HPOCodedValue {

    A,
    L,
    N,
    H,
    NP,
    P,
    U;

    public static final String CODESYSTEM = "http://jax.org/loinc2hpo";
    public String getSystem(){
        return CODESYSTEM;
    }

    public static Loinc2HPOCodedValue fromCode(String codeString) throws UnrecognizedCodeException{
        if (codeString == null || codeString.isEmpty()) {
            return null;
        }
        if (codeString.equals("A")) {
            return A;
        }
        if (codeString.equals("L")) {
            return L;
        }
        if (codeString.equals("N")) {
            return N;
        }
        if (codeString.equals("H")) {
            return H;
        }
        if (codeString.equals("NP")) {
            return NP;
        }
        if (codeString.equals("P")) {
            return P;
        }
        if (codeString.equals("U")) {
            return U;
        }
        throw new UnrecognizedCodeException("Cannot recognize the code: " + codeString);
    }

    public String toCode(){
        switch(this) {
            case A: return "A";
            case L: return "L";
            case N: return "N";
            case H: return "H";
            case NP: return "NP";
            case P: return "P";
            case U: return "U";
            default: return "?";
        }
    }

    public String getDisplay(){
        switch(this){
            case A: return "abnormal";
            case L: return "below normal range";
            case N: return "within normal range";
            case H: return "above normal range";
            case NP: return "not present";
            case P: return "present";
            case U: return "unknown code";
            default: return "?";
        }
    }

    public String getDefinition(){
        //not support now
        return null;
    }


}

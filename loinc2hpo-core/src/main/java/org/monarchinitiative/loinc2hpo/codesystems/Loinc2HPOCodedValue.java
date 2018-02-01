package org.monarchinitiative.loinc2hpo.codesystems;

public enum Loinc2HPOCodedValue {

    L,
    N,
    H,
    NP,
    P,
    U;

    public static String getSystem(){
        return "http://jax.org/loinc2hpo";
    }

    public static Loinc2HPOCodedValue fromCode(String codeString) throws Exception{
        if (codeString == null || codeString.isEmpty()) {
            return null;
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
        throw new Exception("Cannot recognize the code: " + codeString);
    }

    public String toCode(){
        switch(this) {
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

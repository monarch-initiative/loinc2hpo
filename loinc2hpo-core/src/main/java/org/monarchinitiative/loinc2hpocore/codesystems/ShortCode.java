package org.monarchinitiative.loinc2hpocore.codesystems;

import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;

/**
 * These are the code used for the results of lab tests. Other codes, e.g., FHIR, should be
 * mapped onto these codes for annotation of data.
 */
public enum ShortCode {
    L("below normal range"),
    N("within normal range"),
    H("above normal range"),
    NEG("negative"),
    POS("positive"),
    NOM("nominal"),
    U("unknown code");



    private final String name;

    ShortCode(String label) {
            this.name = label;
    }


    public static ShortCode fromShortCode(String codeString) throws Loinc2HpoRuntimeException {
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
        if (codeString.equals("NEG")) {
            return NEG;
        }
        if (codeString.equals("POS")) {
            return POS;
        }
        if (codeString.equals("NOM")) {
            return NOM;
        }
        if (codeString.equals("U")) {
            return U;
        }
        throw Loinc2HpoRuntimeException.unrecognizedCode(codeString);
    }

    public String getName() {
        return name;
    }

    public String shortForm() {
        switch (this) {
            case L: return "L";
            case H: return "H";
            case N: return "N";
            case NOM: return "NOM";
            case POS: return "POS";
            case NEG: return "NEG";
            case U: return "U";
        }
        // needed by compiler, will never happen unless a new constant is added
        throw new Loinc2HpoRuntimeException("Could not find short form");
    }
}

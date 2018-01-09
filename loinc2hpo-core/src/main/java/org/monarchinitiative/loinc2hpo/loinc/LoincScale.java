package org.monarchinitiative.loinc2hpo.loinc;

public enum LoincScale {
    Qn, Unknown;



    public static LoincScale string2enum(String s) {
        s=s.toLowerCase();
        switch (s) {
            case "qn" : return Qn;
            default: return Unknown;
        }
    }
}

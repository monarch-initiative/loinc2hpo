package org.monarchinitiative.loinc2hpocore.loinc;

public enum LoincScale {
    Qn,
    Ord,
    OrdQn,
    Nom,
    Nar,
    Multi,
    Doc,
    Set,
    Unknown;

    public static LoincScale fromString(String s) {
        s=s.toLowerCase();
        switch (s) {
            case "qn" : return Qn;
            case "ord": return Ord;
            case "ordqn": return OrdQn;
            case "nom" : return Nom;
            case "nar" : return Nar;
            case "multi" : return Multi;
            case "doc" : return Doc;
            case "set" : return Set;
            default: return Unknown;
        }
    }
}

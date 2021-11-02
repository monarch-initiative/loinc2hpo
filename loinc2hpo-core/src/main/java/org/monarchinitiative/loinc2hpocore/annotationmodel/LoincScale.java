package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;

public enum LoincScale {

    QUANTITATIVE("Qn"), ORDINAL("Ord"), NOMINAL("Nom");

    private final String name;

    LoincScale(String label) {
        this.name = label;
    }

    public static LoincScale fromString(String scale) {
        switch (scale) {
            case "Qn": return QUANTITATIVE;
            case "Ord": return ORDINAL;
            case "Nom": return NOMINAL;
            default:
                throw new Loinc2HpoRuntimeException("MalformedScale: \"" + scale + "\".");
        }
    }

}

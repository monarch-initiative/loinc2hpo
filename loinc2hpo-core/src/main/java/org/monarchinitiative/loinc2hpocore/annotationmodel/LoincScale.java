package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;

/**
 * Scales of measurement used by LOINC codes. For this library, we only use Qn, Ord, and Nom.
 * @author Aaron Zhang, Peter Robinson
 */
public enum LoincScale {

    QUANTITATIVE("Qn"),
    ORDINAL("Ord"),
    NOMINAL("Nom"),
    /** Test can be reported as either ORD or QN */
    OrdQn("OrdQn"),
    /** Narrative */
    Nar("Nar"),
    /** multi-valued */
    Multi("Multi"),
    Doc("Doc"),
    Set("Set"),
    Unknown("Unknown");
    /*

   case "qn" : return Qn;
            case "ord": return Ord;
            case "ordqn": return OrdQn;
            case "nom" : return Nom;
            case "nar" : return Nar;
            case "multi" : return Multi;
            case "doc" : return Doc;
            case "set" : return Set;
            default: return Unknown;
     */

    private final String name;

    LoincScale(String label) {
        this.name = label;
    }

    public static LoincScale fromString(String scale) {
        switch (scale) {
            case "Qn": return QUANTITATIVE;
            case "Ord": return ORDINAL;
            case "Nom": return NOMINAL;
            case "OrdQn": return OrdQn;
            case "Nar": return Nar;
            case "Multi": return Multi;
            case "Doc": return Doc;
            case "Set": return Set;
            case "Unknown": return Unknown;
            default:
                throw new Loinc2HpoRuntimeException("MalformedScale: \"" + scale + "\".");
        }
    }

}

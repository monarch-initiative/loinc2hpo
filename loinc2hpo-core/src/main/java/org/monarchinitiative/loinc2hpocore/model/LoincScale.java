package org.monarchinitiative.loinc2hpocore.model;

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
    /** some loinc entries such as 70871-9 do not have a scale and just show a dash. */
    Dash("-"),
    /** rare entries have an asterisk. */
    Asterisk("*"),
    Unknown("Unknown");

    private final String name;

    LoincScale(String label) {
        this.name = label;
    }

    public String shortName() { return this.name; }

    public static LoincScale fromString(String scale) {
        return switch (scale) {
            case "Qn" -> QUANTITATIVE;
            case "Ord" -> ORDINAL;
            case "Nom" -> NOMINAL;
            case "OrdQn" -> OrdQn;
            case "Nar" -> Nar;
            case "Multi" -> Multi;
            case "Doc" -> Doc;
            case "Set" -> Set;
            case "-" -> Dash;
            case "*" -> Asterisk;
            case "Unknown" -> Unknown;
            default -> throw new Loinc2HpoRuntimeException("MalformedScale: \"" + scale + "\".");
        };
    }

    /**
     * We are only using Qn, Ord, and Nom scale LOINC codes. We will disregard other codes
     * @return true if this is a scale type (Qn, Ord, Nom) that is usable for LOINC2HPO
     */
    public boolean validForLoinc2Hpo() {
        return this.equals(QUANTITATIVE) || this.equals(ORDINAL) || this.equals(NOMINAL);
    }

}

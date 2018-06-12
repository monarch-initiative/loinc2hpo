package org.monarchinitiative.loinc2hpo.loinc;

/**
 * Indicate the conditionality of a LOINC test in a panel.
 * Refer to LOINC manual, p92-93 "Representing conditionality"
 */
public enum PanelComponentConditionality {
    R,      //required
    R_a,    //required, but an alternative test can substitute
    C,      //conditional on other factors
    O,      //optional
    Rflex,  //reflex condition on other tests
    Rflex_a,//reflex condition on other tests, but an alternative can substitute
    U; //unknown

    public static PanelComponentConditionality of(String code) {
        switch (code) {
            case "R":
                return PanelComponentConditionality.R;
            case "R_a":
                return PanelComponentConditionality.R_a;
            case "C":
                return PanelComponentConditionality.C;
            case "O":
                return PanelComponentConditionality.O;
            case "Rflex":
                return PanelComponentConditionality.Rflex;
            case "Rflex_a":
                return PanelComponentConditionality.Rflex_a;
            default:
                return PanelComponentConditionality.U;
        }
    }
}

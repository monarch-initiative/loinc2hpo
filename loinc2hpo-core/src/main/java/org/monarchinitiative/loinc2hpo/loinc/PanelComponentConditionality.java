package org.monarchinitiative.loinc2hpo.loinc;

/**
 * Indicate the conditionality of a LOINC test in a panel.
 * Refer to LOINC manual, P92-93 "Representing conditionality"
 */
public enum PanelComponentConditionality {
    R,      //required
    R_a,    //required, but an alternative test can substitute
    C,      //conditional on other factors
    O,      //optional
    Rflx,  //reflex condition on other tests
    Rflx_a,//reflex condition on other tests, but an alternative can substitute
    U; //unknown

    public static PanelComponentConditionality of(String code) {
        switch (code) {
            case "R":
                return PanelComponentConditionality.R;
            case "R-a":
                return PanelComponentConditionality.R_a;
            case "R_a":
                return PanelComponentConditionality.R_a;
            case "C":
                return PanelComponentConditionality.C;
            case "O":
                return PanelComponentConditionality.O;
            case "Rflx":
                return PanelComponentConditionality.Rflx;
            case "Rflx_a":
                return PanelComponentConditionality.Rflx_a;
            case "Rflx-a":
                return PanelComponentConditionality.Rflx_a;
            default:
                return PanelComponentConditionality.U;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case R:
                return "R";
            case R_a:
                return "R_a";
            case C:
                return "C";
            case O:
                return "O";
            case Rflx:
                return "Rflx";
            case Rflx_a:
                return "Rflx_a";
            case U:
                return "U";
                default:
                    return "U";
        }
    }
}

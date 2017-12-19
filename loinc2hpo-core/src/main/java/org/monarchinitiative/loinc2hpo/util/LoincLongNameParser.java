package org.monarchinitiative.loinc2hpo.util;

//import java.text.ParseException;

public class LoincLongNameParser {

    private final String loincLongName;
    private String parameter = "";
    private String tissue = "";
    private String assayMethod = "";
    private String assayType = "";

    public LoincLongNameParser(String longname) {
        if (longname == null) {
            throw new IllegalArgumentException("Provide the Long Name of a loinc code!");
        }
        this.loincLongName = longname;
        //split loinc long common name by "by"
        //example: "Erythrocyte distribution width [Ratio] by Automated count"
        //example: "Platelets [#/volume] in Blood by Automated count"
        String[] elems1 = this.loincLongName.split(" by ");
        if (elems1.length == 2) {
            this.assayMethod = elems1[1]; //this is the method for the Loinc assay
        }
        String[] elems2 = elems1[0].split(" in ");
        if(elems2.length == 1) {//if no "in", then it might be "of". example: Hematocrit [Volume Fraction] of Blood
            elems2 = elems2[0].split(" of ");
        }
        if(elems2.length == 1) {
            String[] elems3 = elems2[0].split(" \\[");
            if (elems3.length == 1) {
                this.parameter = elems3[0];
            }
            if (elems3.length == 2) {
                this.parameter = elems3[0];
                this.assayType = elems3[1].split("\\]")[0];
            }
        }
        if(elems2.length == 3) { //e.g. "Cholesterol in HDL [Presence] in Serum or Plasma"
            elems2 = new String[]{elems2[0] + " in " + elems2[1], elems2[2]};
        }
        if(elems2.length == 2) { //"Platelets [#/volume]", "Blood"
            this.tissue = elems2[1];
            String[] elems3 = elems2[0].split(" \\[");
            if (elems3.length == 1) {
                this.parameter = elems3[0];
            }
            if (elems3.length == 2) {
                this.parameter = elems3[0];
                this.assayType = elems3[1].split("\\]")[0];
                if(elems3[1].split("\\]").length == 2) {
                    this.parameter = this.parameter + elems3[1].split("\\]")[1];
                }
            }
        }

    }

    /**
     * Extract and return the parameter from a Loinc long common name.
     * e.g. "Platelet mean volume [Entitic volume] in Blood by Automated count"
     * The method will return "Platelet mean volume".
     * @return parameter measured from a Loinc long common name; "" if such information is not identified.
     */
    public String getLoincParameter(){

        return this.parameter;

    }

    /**
     * Extract and return the tissue from a Loinc long common name.
     * e.g. "Platelet mean volume [Entitic volume] in Blood by Automated count"
     * The method will return "Blood".
     * @return tissued measured from a Loinc long common name; "" if such information is not identified.
     */
    public String getLoincTissue(){

        return this.tissue;

    }

    /**
     * Extract and return the method from a Loinc long common name.
     * e.g. "Platelet mean volume [Entitic volume] in Blood by Automated count"
     * The method will return "Automated count".
     * @return method used from a Loinc long common name; "" if such information is not identified.
     */
    public String getLoincMethod(){

        return this.assayMethod;
    }

    /**
     * Extract and return the type of a Loinc assay from a Loinc long common name.
     * e.g. "Platelet mean volume [Entitic volume] in Blood by Automated count"
     * The method will return "Entitic volume" (Strings in brackets).
     * @return type used from a Loinc long common name; "" if such information is not identified.
     */
    public String getLoincType(){

        return this.assayType;

    }

    /**
     * Override the toString method to get the complete information.
     * @return information identified from a Loinc long common name.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.loincLongName + ": \n");
        builder.append("parameter: " + this.parameter + "\n");
        builder.append("tissue: " + this.tissue + "\n");
        builder.append("assay method: " + this.assayMethod + "\n");
        builder.append("assay type: " + this.assayType + "\n");
        return builder.toString();
    }

}

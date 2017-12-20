package org.monarchinitiative.loinc2hpo.util;

//import java.text.ParseException;

public class LoincLongNameParser {


    public static LoincCodeClass parse(String loincLongName) {

        String parameter = "";
        String tissue = "";
        String assayMethod = "";
        String assayType = "";


        if (loincLongName == null) {
            throw new IllegalArgumentException("Provide the Long Name of a loinc code!");
        }
        //split loinc long common name by "by"
        //example: "Erythrocyte distribution width [Ratio] by Automated count"
        //example: "Platelets [#/volume] in Blood by Automated count"
        String[] elems1 = loincLongName.split(" by ");
        if (elems1.length == 2) {
            assayMethod = elems1[1]; //this is the method for the Loinc assay
        }
        String[] elems2 = elems1[0].split(" in ");
        if(elems2.length == 1) {//if no "in", then it might be "of". example: Hematocrit [Volume Fraction] of Blood
            elems2 = elems2[0].split(" of ");
        }
        if(elems2.length == 1) {
            String[] elems3 = elems2[0].split(" \\[");
            if (elems3.length == 1) {
                parameter = elems3[0];
            }
            if (elems3.length == 2) {
                parameter = elems3[0];
                assayType = elems3[1].split("\\]")[0];
            }
        }
        if(elems2.length == 3) { //e.g. "Cholesterol in HDL [Presence] in Serum or Plasma"
            elems2 = new String[]{elems2[0] + " in " + elems2[1], elems2[2]};
        }
        if(elems2.length == 2) { //"Platelets [#/volume]", "Blood"
            tissue = elems2[1];
            String[] elems3 = elems2[0].split(" \\[");
            if (elems3.length == 1) {
                parameter = elems3[0];
            }
            if (elems3.length == 2) {
                parameter = elems3[0];
                assayType = elems3[1].split("\\]")[0];
                if(elems3[1].split("\\]").length == 2) {
                    parameter = parameter + elems3[1].split("\\]")[1];
                }
            }
        }

        return new LoincCodeClass(parameter, tissue, assayMethod, assayType);
    }



    /**

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
    **/

}

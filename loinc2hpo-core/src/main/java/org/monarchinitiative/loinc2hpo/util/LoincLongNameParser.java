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
        String[] elems1 = this.loincLongName.split("by");
        if (elems1.length == 2) {
            this.assayMethod = elems1[1]; //this is the method for the Loinc assay
        }
        String[] elems2 = elems1[0].split("in");
        if(elems2.length == 1) {//if no "in", then it might be "of". example: Hematocrit [Volume Fraction] of Blood
            elems2 = elems2[0].split("of");
        }
        if(elems2.length == 1) {
            String[] elems3 = elems2[0].split("\\[");
            if (elems3.length == 2) {
                this.parameter = elems3[0];
                this.assayType = elems3[1].split("\\]")[0];
            }
        }
        if(elems2.length == 2) { //"Platelets [#/volume]", "Blood"
            this.tissue = elems2[1];
            String[] elems3 = elems2[0].split("\\[");
            if (elems3.length == 2) {
                this.parameter = elems3[0];
                this.assayType = elems3[1].split("\\]")[0];
            }
        }

    }

    public String getLoincParameter(){

//        if(this.parameter == null) {
//            throw new ParseException("Loinc Parameter is not found!", 1);
//        }
        return this.parameter;

    }

    public String getLoincTissue(){

        return this.tissue;

    }

    public String getLoincMethod(){

        return this.assayMethod;
    }

    public String getLoincType(){

        return this.assayType;

    }

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

    public static void main(String[] args) {
        String name1 = "Potassium [Moles/volume] in Serum or Plasma";
        LoincLongNameParser parser = new LoincLongNameParser(name1);
        System.out.println(parser);

        String name2 = "Erythrocyte distribution width [Ratio] by Automated count";
        parser = new LoincLongNameParser(name2);
        System.out.println(parser);

        String name3 = "Platelet mean volume [Entitic volume] in Blood by Automated count";
        parser = new LoincLongNameParser(name3);
        System.out.println(parser);
    }


}

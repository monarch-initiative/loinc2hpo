package org.monarchinitiative.loinc2hpo.util;

public class LoincCodeClass {

    private String parameter;
    private String tissue;
    private String assayMethod;
    private String assayType;

    public LoincCodeClass(String parameter, String tissue, String method, String type) {
        this.parameter = parameter;
        this.tissue = tissue;
        this.assayMethod = method;
        this.assayType = type;
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
}

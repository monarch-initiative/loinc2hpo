package org.monarchinitiative.loinc2hpo.loinc;

public class LoincObservationResult {

    static enum category { LOW, WITHIN_NORMAL_RANGE, HIGH, ABSENT, PRESENT, UNKNOWN }

    private category cat;


    public category getCategory() { return cat; }



    public LoincObservationResult(String interpretationCode) {
        this.cat=String2Category(interpretationCode);
    }

    //public LoincObservationResult(String value, ReferenceRange range) {}



    public static category String2Category(String interpretationCode) {
        interpretationCode=interpretationCode.toLowerCase();
        switch (interpretationCode) {
            case "l":
            case "low" : return category.LOW;
            case "high":
            case "H":
            case "h":return category.HIGH;
            case "absent": return category.ABSENT;
            case "present": return category.PRESENT;
            case "n":
            case "normal":
            case "wnl": return category.WITHIN_NORMAL_RANGE;

            default: return category.UNKNOWN;
        }
    }


}

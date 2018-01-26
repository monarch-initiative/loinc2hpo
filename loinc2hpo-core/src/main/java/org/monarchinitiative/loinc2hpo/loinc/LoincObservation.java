package org.monarchinitiative.loinc2hpo.loinc;

public class LoincObservation {

    static enum category { LOW, WITHIN_NORMAL_RANGE, HIGH, ABSENT, PRESENT, UNKNOWN }

    private category cat;


    public category getCategory() { return cat; }



    public LoincObservation(String cat, String interpretationString) {
        this.cat=String2Category(cat);
    }

    //public LoincObservation(String value, ReferenceRange range) {}



    public static category String2Category(String cat) {
        cat=cat.toLowerCase();
        switch (cat) {
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

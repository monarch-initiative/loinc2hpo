package org.monarchinitiative.loinc2hpo.loinc;

public class NormalRange {

    private final String lo;
    private final String hi;
    private final String unit;

    public NormalRange(String low, String high, String units) {
        lo=low;
        hi=high;
        unit=units;
    }

    @Override
    public String toString() {
        return String.format("%s-%s [%s]",lo,hi,unit);
    }

}

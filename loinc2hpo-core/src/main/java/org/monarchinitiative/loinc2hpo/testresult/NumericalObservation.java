package org.monarchinitiative.loinc2hpo.testresult;

public class NumericalObservation implements  Observation {

    private final double value;
    private double LOW;
    private double HIGH;

    public NumericalObservation(double v) {
        value=v;
    }

    public void setLOW(double low) {
        this.LOW = low;
    }


}

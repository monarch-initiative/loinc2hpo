package org.monarchinitiative.loinc2hpo.testresult;

public class NumericalObservation implements  Observation {

    private final double value;

    public NumericalObservation(double v) {
        value=v;
    }


}

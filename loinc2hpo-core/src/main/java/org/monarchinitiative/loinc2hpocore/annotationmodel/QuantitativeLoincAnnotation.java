package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;

import java.util.Optional;

public class QuantitativeLoincAnnotation implements LoincAnnotation {


    private final Loinc2HpoAnnotation low;
    private final Loinc2HpoAnnotation normal;
    private final Loinc2HpoAnnotation high;


    public QuantitativeLoincAnnotation(Loinc2HpoAnnotation low,
                                       Loinc2HpoAnnotation normal,
                                       Loinc2HpoAnnotation high) {
        this.low = low;
        this.normal = normal;
        this.high = high;
    }

    @Override
    public Optional<Hpo2Outcome> getAnnotation(Outcome outcome) {
        switch (outcome.getCode()) {
            case L:
                return Optional.of(new Hpo2Outcome(low.getHpoTermId(), ShortCode.L));
            case N:
                return Optional.of(new Hpo2Outcome(normal.getHpoTermId(), ShortCode.N));
            case H:
                return Optional.of(new Hpo2Outcome(high.getHpoTermId(), ShortCode.H));
            default:
                return Optional.empty();
        }
    }
}

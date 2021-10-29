package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;

import java.util.Optional;


public class OrdinalHpoAnnotation implements LoincAnnotation {

    private final Loinc2HpoAnnotation absent;
    private final Loinc2HpoAnnotation present;

    public OrdinalHpoAnnotation(Loinc2HpoAnnotation absent, Loinc2HpoAnnotation present) {
        this.absent = absent;
        this.present = present;
    }


    @Override
    public Optional<Hpo2Outcome> getAnnotation(Outcome outcome) {
        switch(outcome.getCode()) {
            case ABSENT:
                return Optional.of(new Hpo2Outcome(absent.getHpoTermId(), ShortCode.A));
            case PRESENT:
                return Optional.of(new Hpo2Outcome(present.getHpoTermId(), ShortCode.H));
            default:
                return Optional.empty();
        }
    }
}

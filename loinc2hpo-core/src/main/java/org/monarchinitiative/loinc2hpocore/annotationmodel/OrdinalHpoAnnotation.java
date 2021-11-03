package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class OrdinalHpoAnnotation implements LoincAnnotation {
    private final TermId loincId;
    private final Loinc2HpoAnnotation absent;
    private final Loinc2HpoAnnotation present;

    public OrdinalHpoAnnotation(Loinc2HpoAnnotation absent, Loinc2HpoAnnotation present) {
        this.absent = absent;
        this.present = present;
        // assumption is that both annotation have the same LoincId, which will be true
        // unless there is some insanity
        this.loincId = this.absent.getLoincId();
    }


    @Override
    public Optional<Hpo2Outcome> getOutcome(Outcome outcome) {
        switch(outcome.getCode()) {
            case ABSENT:
                return Optional.of(new Hpo2Outcome(absent.getHpoTermId(), ShortCode.A));
            case PRESENT:
                return Optional.of(new Hpo2Outcome(present.getHpoTermId(), ShortCode.H));
            default:
                return Optional.empty();
        }
    }

    @Override
    public TermId getLoincId() {
        return this.loincId;
    }

    @Override
    public List<Loinc2HpoAnnotation> allAnnotations() {
        return List.of(absent, present);
    }
}

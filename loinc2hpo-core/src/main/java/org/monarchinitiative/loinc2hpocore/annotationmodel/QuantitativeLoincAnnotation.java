package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Optional;

public class QuantitativeLoincAnnotation implements LoincAnnotation {

    private final LoincId loincId;
    private final Loinc2HpoAnnotation low;
    private final Loinc2HpoAnnotation normal;
    private final Loinc2HpoAnnotation high;


    public QuantitativeLoincAnnotation(Loinc2HpoAnnotation low,
                                       Loinc2HpoAnnotation normal,
                                       Loinc2HpoAnnotation high) {
        this.low = low;
        this.normal = normal;
        this.high = high;
        // assumption is that both annotation have the same LoincId, which will be true
        // unless there is some insanity
        this.loincId = this.low.getLoincId();
    }

    @Override
    public Optional<Hpo2Outcome> getOutcome(Outcome outcome) {
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

    @Override
    public LoincId getLoincId() {
        return this.loincId;
    }

    @Override
    public List<Loinc2HpoAnnotation> allAnnotations() {
        return List.of(low, normal, high);
    }
}

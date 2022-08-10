package org.monarchinitiative.loinc2hpocore.impl;

import org.monarchinitiative.loinc2hpocore.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class OrdinalHpoAnnotation implements LoincAnnotation {
    private final LoincId loincId;
    private final Loinc2HpoAnnotation negative;
    private final Loinc2HpoAnnotation positive;

    public OrdinalHpoAnnotation(Loinc2HpoAnnotation absent, Loinc2HpoAnnotation present) {
        this.negative = absent;
        this.positive = present;
        // assumption is that both annotation have the same LoincId, which will be true
        // unless there is some insanity
        this.loincId = this.negative.getLoincId();
    }


    @Override
    public Optional<Hpo2Outcome> getOutcome(Outcome outcome) {
        return switch (outcome.getCode()) {
            case NEG -> Optional.of(new Hpo2Outcome(negative.getHpoTermId(), Outcome.NEGATIVE()));
            case POS -> Optional.of(new Hpo2Outcome(positive.getHpoTermId(), Outcome.POSITIVE()));
            default -> Optional.empty();
        };
    }

    @Override
    public LoincId getLoincId() {
        return this.loincId;
    }

    @Override
    public List<Loinc2HpoAnnotation> allAnnotations() {
        return List.of(negative, positive);
    }

    private static String getDebugInfo(Map<Outcome, Loinc2HpoAnnotation> outcomeMap) {
        StringBuilder sb = new StringBuilder("Malformed Ordinal outcomes\nn=").append(outcomeMap.size());
        for (var oc : outcomeMap.values()) {
            sb.append("\t[ERROR] ").append(oc).append("\n");
        }
        return sb.toString();
    }


    public static LoincAnnotation fromOutcomeMap(Map<Outcome, Loinc2HpoAnnotation> outcomeMap) {
        // there are only two possible Ordinal outcomes, so we just need to check for size
        if (outcomeMap.size() == 2 && outcomeMap.containsKey(Outcome.NEGATIVE()) && outcomeMap.containsKey(Outcome.POSITIVE())) {
            return new OrdinalHpoAnnotation(outcomeMap.get(Outcome.NEGATIVE()), outcomeMap.get(Outcome.POSITIVE()));
        } else {
            String msg = String.format("Could not create LoincAnnotation for ordinal: %s",
                    getDebugInfo(outcomeMap));
            throw new Loinc2HpoRuntimeException(msg);
        }
    }

    @Override
    public LoincScale scale() {
        return LoincScale.ORDINAL;
    }

    @Override
    public String toString() {
        return loincId + "\n\t" +
                (negative == null? " NEG: n/a" : "NEG: " + negative) + "\n\t" +
                (positive == null? " POS: n/a" : "POS: " + positive);
    }
}

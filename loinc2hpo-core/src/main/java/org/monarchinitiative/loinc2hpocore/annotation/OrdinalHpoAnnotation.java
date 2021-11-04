package org.monarchinitiative.loinc2hpocore.annotation;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class OrdinalHpoAnnotation implements LoincAnnotation {
    private final LoincId loincId;
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
        switch (outcome.getCode()) {
            case ABSENT:
                return Optional.of(new Hpo2Outcome(absent.getHpoTermId(), ShortCode.A));
            case PRESENT:
                return Optional.of(new Hpo2Outcome(present.getHpoTermId(), ShortCode.H));
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
        return List.of(absent, present);
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
        if (outcomeMap.size() == 2 && outcomeMap.containsKey(Outcome.ABSENT()) && outcomeMap.containsKey(Outcome.PRESENT())) {
            return new OrdinalHpoAnnotation(outcomeMap.get(Outcome.ABSENT()), outcomeMap.get(Outcome.PRESENT()));
        } else {
            String msg = String.format("Could not create LoincAnnotation for ordinal: %s",
                    getDebugInfo(outcomeMap));
            throw new Loinc2HpoRuntimeException(msg);
        }
    }
}

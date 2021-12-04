package org.monarchinitiative.loinc2hpocore.annotation;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class is for LOINC2HPO annotations with the {@code Qn} scale. Most of these annotations
 * have values for Low, Normal, and High results, but some only have two relevant values. For instance,
 * LOINC 6047-5 (Bloodworm IgE Ab [Units/volume] in Serum) does not have a "low" value, because there
 * is no abnormal low level of this analyte. In this case, we use the fromNormalAndHigh factory method,
 * store a null pointer for {@link #low} and return Optional.empty() for this value (which should actually
 * never be queried in the real world). Similarly, in some occasions we just have Low and Normal.
 * We do expect to have a normal annotation in all cases, and if not, an Exception is thrown.
 * @author Peter Robinson
 */
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
        // assumption is that all annotation have the same LoincId
        // we assume that although some annotation have just two values there will always be a
        // normal value (this is enforced by the parser and not checked here)
        this.loincId = this.normal.getLoincId();
    }


    private static String getDebugInfo(Map<Outcome, Loinc2HpoAnnotation> outcomeMap) {
        StringBuilder sb = new StringBuilder("Malformed Quantitivate outcomes\nn=").append(outcomeMap.size());
        for (var oc : outcomeMap.values()) {
            sb.append("\t[ERROR] ").append(oc).append("\n");
        }
        return sb.toString();
    }




    /**
     * Create between 1 and 3 components. Note that we demand there be a NORMAL annotation,
     * but LOW and HIGH are optional.
     * @param outcomeMap outcomes and annotations for a LOINC test
     * @return corresponding {@link LoincAnnotation} object
     */
    public static LoincAnnotation fromOutcomeMap(Map<Outcome, Loinc2HpoAnnotation> outcomeMap) {
        if (! outcomeMap.containsKey(Outcome.NORMAL())) {
            String msg = String.format("Attempt to create Quantitative LoincAnnotation without Normal annotation: %s",
                    getDebugInfo(outcomeMap));
            throw new Loinc2HpoRuntimeException(msg);
        }
        if (outcomeMap.size() == 3) {
            return new QuantitativeLoincAnnotation(outcomeMap.get(Outcome.LOW()),
                    outcomeMap.get(Outcome.NORMAL()),
                    outcomeMap.get(Outcome.HIGH()));
        } else if (outcomeMap.size() == 2 &&
                outcomeMap.containsKey(Outcome.NORMAL()) &&
                outcomeMap.containsKey(Outcome.HIGH())) {
            return new QuantitativeLoincAnnotation(null, outcomeMap.get(Outcome.NORMAL()),
                    outcomeMap.get(Outcome.HIGH()));
        } else if (outcomeMap.size() == 2 &&
                outcomeMap.containsKey(Outcome.LOW()) &&
                outcomeMap.containsKey(Outcome.NORMAL())) {
            return new QuantitativeLoincAnnotation(outcomeMap.get(Outcome.LOW()),
                    outcomeMap.get(Outcome.NORMAL()), null);
        } else if (outcomeMap.size() == 1 && outcomeMap.containsKey(Outcome.NORMAL())) {
            return new QuantitativeLoincAnnotation(null, outcomeMap.get(Outcome.NORMAL()), null);
        }
        String msg = String.format("\"Unable to create Quantitative LoincAnnotation  annotation: %s",
                getDebugInfo(outcomeMap));
        throw new Loinc2HpoRuntimeException(msg);
    }



    @Override
    public Optional<Hpo2Outcome> getOutcome(Outcome outcome) {
        switch (outcome.getCode()) {
            case L:
                if (low == null) return Optional.empty();
                else return Optional.of(new Hpo2Outcome(low.getHpoTermId(), Outcome.LOW()));
            case N:
                return Optional.of(new Hpo2Outcome(normal.getHpoTermId(), Outcome.NORMAL()));
            case H:
                if (high == null) return Optional.empty();
                return Optional.of(new Hpo2Outcome(high.getHpoTermId(), Outcome.HIGH()));
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
        List<Loinc2HpoAnnotation> allAnnots = new ArrayList<>();
        if (low != null) {
            allAnnots.add(low);
        }
        allAnnots.add(normal);
        if (high != null) {
            allAnnots.add(high);
        }
        return allAnnots;
    }
}

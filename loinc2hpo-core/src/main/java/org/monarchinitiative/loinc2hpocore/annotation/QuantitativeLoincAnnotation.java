package org.monarchinitiative.loinc2hpocore.annotation;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.List;
import java.util.Optional;

/**
 * This class is for LOINC2HPO annotations with the {@code Qn} scale. Most of these annotations
 * have values for Low, Normal, and High results, but some only have two relevant values. For instance,
 * LOINC 6047-5 (Bloodworm IgE Ab [Units/volume] in Serum) does not have a "low" value, because there
 * is no abnormal low level of this analyte. In this case, we use the fromNormalAndHigh factory method,
 * store a null pointer for {@link #low} and return Optional.empty() for this value (which should actually
 * never be queried in the real world).
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

    public static QuantitativeLoincAnnotation fromNormalAndHigh(Loinc2HpoAnnotation normal,
                                                                Loinc2HpoAnnotation high) {
        return new QuantitativeLoincAnnotation(null, normal, high);
    }


    @Override
    public Optional<Hpo2Outcome> getOutcome(Outcome outcome) {
        switch (outcome.getCode()) {
            case L:
                if (low == null) return Optional.empty();
                else return Optional.of(new Hpo2Outcome(low.getHpoTermId(), ShortCode.L));
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

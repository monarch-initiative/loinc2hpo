package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Observation;
import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ObservationR4 implements Uberobservation {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationR4.class);
    private final org.hl7.fhir.r4.model.Observation observation;


    public ObservationR4(org.hl7.fhir.r4.model.Observation observation) {
        this.observation = observation;
    }

    @Override
    public Optional<LoincId> getLoincId() {
        LoincId loincId;
        for (Coding coding : observation.getCode().getCoding()) {
            if (coding.getSystem().equals("http://loinc.org")) {
                loincId = new LoincId(coding.getCode());
                return Optional.of(loincId);
            }
        }
        return Optional.empty();
    }

    private Outcome getOutcome(ShortCode code, org.hl7.fhir.r4.model.Observation observation) {
        if (code.equals(ShortCode.NOM)) {
            throw new UnsupportedOperationException("TODO");
        }
        switch (code) {
            case H:
                return Outcome.HIGH();
            case L:
                return Outcome.LOW();
            case N:
                return Outcome.NORMAL();
            case NEG:
                return Outcome.NEGATIVE();
            case POS:
                return Outcome.POSITIVE();
            default:
                throw new UnsupportedOperationException("TODO");
        }
    }


    @Override
    public Optional<Outcome> getOutcome() {
        if (observation.hasInterpretation()) {
            List<String> codes = this.observation.getInterpretation().stream()
                    .distinct()
                    .map(CodeableConcept::getCoding)
                    .flatMap(Collection::stream)
                    .map(Coding::getCode)
                    .collect(Collectors.toList());
            if (codes.size() > 1) {
                LOGGER.error("Multiple interpretation codes returned");
                return Optional.empty();
            }
            ShortCode code = ShortCode.fromShortCode(codes.get(0));
            Outcome outcome = getOutcome(code, observation);
            return Optional.of(outcome);
        } else if (observation.hasValueCodeableConcept()) {
            return getOutcomeFromCodedValue();
        } else if (observation.hasValueQuantity()) {
            return getOutcomeFromValueQuantity();
        } else {
            LOGGER.error("Unable to handle observation {}", observation);
            return Optional.empty();
        }
    }

    Optional<Outcome> getOutcomeFromCodedValue() {
        CodeableConcept codeableConcept = this.observation.getValueCodeableConcept();
        if (codeableConcept == null) { // should never happen
            LOGGER.error("Codable concept null in getOutcomeFromCodedValue");
        }
        List<Coding> codings = codeableConcept != null ? codeableConcept.getCoding() : List.of();
        for (Coding coding : codings) {
            String code = coding.getCode();
            String system = coding.getSystem();
            String display = coding.getDisplay();
            String outcomeString = code + ":" + system + ":" + display;
            Outcome outcome = Outcome.nominal(outcomeString);
            return Optional.of(outcome);
        }
        return Optional.empty();
    }

    Optional<Outcome> getOutcomeFromValueQuantity() {
        List<Observation.ObservationReferenceRangeComponent> references =
                this.observation.getReferenceRange();

        if (references.size() == 0) {
            LOGGER.error("Reference range not found");
            return Optional.empty();
        }

        if (references.size() >= 2) {
            LOGGER.error("Reference range had more than two entries");
            return Optional.empty();
            // TODO sometimes this is observed.
            //An exception: three reference sizes
            //it can happen when there is actually one range but coded in three ranges
            //e.g. normal 20-30
            //in this case, one range ([20, 30]) is sufficient;
            //however, it is written as three ranges: ( , 20) [20, 30] (30, )
        }
        Observation.ObservationReferenceRangeComponent targetReference = references.get(0);
        double low = targetReference.hasLow() ?
                targetReference.getLow().getValue().doubleValue() : Double.MIN_VALUE;
        double high = targetReference.hasHigh() ?
                targetReference.getHigh().getValue().doubleValue() : Double.MAX_VALUE;
        double observed = this.observation.getValueQuantity().getValue().doubleValue();
        if (observed < low) {
            return Optional.of(Outcome.LOW());
        } else if (observed > high) {
            return Optional.of(Outcome.HIGH());
        } else {
            return Optional.of(Outcome.NORMAL());
        }
    }
}

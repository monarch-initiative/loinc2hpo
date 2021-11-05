package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.apache.commons.lang.NotImplementedException;
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
            throw new NotImplementedException("TODO");
        }
        switch (code) {
            case H: return Outcome.HIGH();
            case L: return Outcome.LOW();
            case N: return Outcome.NORMAL();
            case ABSENT: return Outcome.ABSENT();
            case PRESENT: return Outcome.PRESENT();
            case A:  throw new NotImplementedException("TODO");
            default:
                throw new NotImplementedException("TODO");
        }
    }


    @Override
    public Optional<Outcome> getOutcome() {
        if (observation.hasInterpretation()){
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
        } /*else if (observation.hasValueCodeableConcept()){
            result = new ObservationAnalysisFromCodedValues(loinc2Hpo,
                    observation).getHPOforObservation();
        } else if (observation.hasValueQuantity()){
            result = new ObservationAnalysisFromQnValue(loinc2Hpo,
                    observation).getHPOforObservation();
        } else {
            logger.info("Unable to handle observation");
            return Optional.empty();
        } */
        return Optional.empty();
    }
}

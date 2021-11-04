package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Hpo2Outcome;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;

public class FhirObservation2Hpo {
    private static final Logger logger = LoggerFactory.getLogger(FhirObservation2Hpo.class);

    private final Loinc2Hpo loinc2Hpo;
    private final Set<LoincId> loincIdSet;

    public FhirObservation2Hpo(Loinc2Hpo loinc2Hpo, Set<LoincId> loincIdSet) {
        this.loinc2Hpo = loinc2Hpo;
        this.loincIdSet = loincIdSet;
    }

    public Optional<Hpo2Outcome> fhir2hpo(Observation observation) {
//        LoincId loincId = FhirObservationUtil.getLoincIdOfObservation(observation);
//        if (!loincIdSet.contains(loincId)){
//            throw Loinc2HpoRuntimeException.unrecognizedLoincCodeException();
//        }
//        Hpo2Outcome result;
        /*
        if (observation.hasInterpretation()){
            result = new ObservationAnalysisFromInterpretation(loinc2Hpo,
                    observation).getHPOforObservation();
        } else if (observation.hasValueCodeableConcept()){
            result = new ObservationAnalysisFromCodedValues(loinc2Hpo,
                    observation).getHPOforObservation();
        } else if (observation.hasValueQuantity()){
            result = new ObservationAnalysisFromQnValue(loinc2Hpo,
                    observation).getHPOforObservation();
        } else {
            logger.info("Unable to handle observation");
            return Optional.empty();
        }


        return Optional.of(result);

         */
        // TODO
        return Optional.empty();
    }

}

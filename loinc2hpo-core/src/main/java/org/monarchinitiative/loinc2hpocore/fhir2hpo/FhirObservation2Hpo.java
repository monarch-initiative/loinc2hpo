package org.monarchinitiative.loinc2hpocore.fhir2hpo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.annotationmodel.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.exception.*;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;


import java.util.*;

public class FhirObservation2Hpo {

    static Logger logger = LogManager.getLogger();

    private Loinc2Hpo loinc2Hpo;
    private Set<LoincId> loincIdSet;

    public FhirObservation2Hpo(Loinc2Hpo loinc2Hpo, Set<LoincId> loincIdSet) {
        this.loinc2Hpo = loinc2Hpo;
        this.loincIdSet = loincIdSet;
    }

    public HpoTerm4TestOutcome fhir2hpo(Observation observation) throws LoincCodeNotFoundException, MalformedLoincCodeException, UnrecognizedLoincCodeException, AmbiguousResultsFoundException, AnnotationNotFoundException, LoincCodeNotAnnotatedException, UnrecognizedCodeException, AmbiguousReferenceException, ReferenceNotFoundException {
        LoincId loincId =
                FhirObservationUtil.getLoincIdOfObservation(observation);
        if (!loincIdSet.contains(loincId)){
            throw new UnrecognizedLoincCodeException();
        }

        HpoTerm4TestOutcome result;
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
            return null;
        }
        return result;
    }

}

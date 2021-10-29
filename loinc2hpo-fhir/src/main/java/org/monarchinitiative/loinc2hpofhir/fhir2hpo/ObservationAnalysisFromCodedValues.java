package org.monarchinitiative.loinc2hpofhir.fhir2hpo;


import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.codesystems.OutcomeCodeOLD;
import org.monarchinitiative.loinc2hpocore.exception.*;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Hpo2Outcome;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotationModelLEGACY;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ObservationAnalysisFromCodedValues implements ObservationAnalysis {

    private static final Logger logger = LoggerFactory.getLogger(ObservationAnalysisFromCodedValues.class);

    private LoincId loincId;
    private CodeableConcept codedValue;
    private Map<LoincId, Loinc2HpoAnnotationModelLEGACY> annotationMap;

    private final Loinc2Hpo loinc2Hpo;
    private final Observation observation;

    public ObservationAnalysisFromCodedValues(Loinc2Hpo loinc2Hpo,
                                              Observation observation){
        this.loinc2Hpo = loinc2Hpo;
        this.observation = observation;
    }

    @Override
    public Hpo2Outcome getHPOforObservation() throws Exception {
        return null;
    }

//    public ObservationAnalysisFromCodedValues(LoincId loincId, CodeableConcept codedvalue, Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap) {
//        this.loincId = loincId;
//        this.codedValue = codedvalue;
//        this.annotationMap = annotationMap;
//    }
//
//    public ObservationAnalysisFromCodedValues(LoincId loincId, Observation observation, Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap) {
//        this.loincId = loincId;
//        try {
//            this.codedValue = observation.getValueCodeableConcept();
//        } catch (FHIRException e) {
//            //should not allow this to happen. Check an observation has a coded value before calling this constructor
//            logger.error("Anticipating a coded value but found none.");
//        }
//        this.annotationMap = annotationMap;
//    }

/*
    @Override
    public Hpo2Outcome getHPOforObservation() throws Loinc2HpoRuntimeException {

        LoincId loincId =
                FhirObservationUtil.getLoincIdOfObservation(this.observation);
        this.annotationMap = loinc2Hpo.getAnnotationMap();
        if (annotationMap.get(loincId) == null) throw Loinc2HpoRuntimeException.notAnnotated(loincId);
        CodeableConcept codedValue = this.observation.getValueCodeableConcept();

        Set<Hpo2Outcome> results = codedValue.getCoding()
                .stream()
                .filter(p -> annotationMap.get(loincId).getCandidateHpoTerms().containsKey(p))
                .map(c -> annotationMap.get(loincId).getCandidateHpoTerms().get(new OutcomeCodeOLD(c.getSystem(),
                        c.getCode(), null)))
                .collect(Collectors.toSet());
        if (results.size() > 1) {
            throw Loinc2HpoRuntimeException.ambiguousResults();
        }
        if (results.size() == 1) {
            return results.iterator().next();
        } else {
            throw Loinc2HpoRuntimeException.noCodeFound();
        }

    }

 */
}

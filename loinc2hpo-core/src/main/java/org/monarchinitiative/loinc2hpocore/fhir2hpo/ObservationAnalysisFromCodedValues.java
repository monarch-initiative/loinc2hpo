package org.monarchinitiative.loinc2hpocore.fhir2hpo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.exception.*;
import org.monarchinitiative.loinc2hpocore.annotationmodel.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotationModel;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ObservationAnalysisFromCodedValues implements ObservationAnalysis {

    private static final Logger logger = LogManager.getLogger();

    private LoincId loincId;
    private CodeableConcept codedValue;
    private Map<LoincId, Loinc2HpoAnnotationModel> annotationMap;

    private Loinc2Hpo loinc2Hpo;
    private Observation observation;

    public ObservationAnalysisFromCodedValues(Loinc2Hpo loinc2Hpo,
                                              Observation observation){
        this.loinc2Hpo = loinc2Hpo;
        this.observation = observation;
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


    @Override
    public HpoTerm4TestOutcome getHPOforObservation() throws AmbiguousResultsFoundException, UnrecognizedCodeException, AnnotationNotFoundException, LoincCodeNotFoundException, MalformedLoincCodeException {

        LoincId loincId =
                FhirObservationUtil.getLoincIdOfObservation(this.observation);

        this.annotationMap = loinc2Hpo.getAnnotationMap();

        if (annotationMap.get(loincId) == null) throw new AnnotationNotFoundException();

        CodeableConcept codedValue = this.observation.getValueCodeableConcept();

        Set<HpoTerm4TestOutcome> results = codedValue.getCoding()
                .stream()
                .filter(p -> annotationMap.get(loincId).getCandidateHpoTerms().keySet().contains(new Code(p)))
                .map(c -> annotationMap.get(loincId).getCandidateHpoTerms().get(new Code(c.getSystem(),
                        c.getCode(), null)))
                .collect(Collectors.toSet());
        if (results.size() > 1) {
            throw new AmbiguousResultsFoundException();
        }
        if (results.size() == 1) {
            return results.iterator().next();
        } else {
            throw new UnrecognizedCodeException();
        }

    }
}

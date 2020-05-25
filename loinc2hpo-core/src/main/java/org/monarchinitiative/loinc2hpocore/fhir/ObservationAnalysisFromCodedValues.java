package org.monarchinitiative.loinc2hpocore.fhir;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.exception.AmbiguousResultsFoundException;
import org.monarchinitiative.loinc2hpocore.exception.AnnotationNotFoundException;
import org.monarchinitiative.loinc2hpocore.exception.UnrecognizedCodeException;
import org.monarchinitiative.loinc2hpocore.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObservationAnalysisFromCodedValues implements ObservationAnalysis {

    private static final Logger logger = LogManager.getLogger();

    private LoincId loincId;
    private CodeableConcept codedValue;
    private Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap;

    public ObservationAnalysisFromCodedValues(LoincId loincId, CodeableConcept codedvalue, Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap) {
        this.loincId = loincId;
        this.codedValue = codedvalue;
        this.annotationMap = annotationMap;
    }

    public ObservationAnalysisFromCodedValues(LoincId loincId, Observation observation, Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap) {
        this.loincId = loincId;
        try {
            this.codedValue = observation.getValueCodeableConcept();
        } catch (FHIRException e) {
            //should not allow this to happen. Check an observation has a coded value before calling this constructor
            logger.error("Anticipating a coded value but found none.");
        }
        this.annotationMap = annotationMap;
    }


    @Override
    public HpoTerm4TestOutcome getHPOforObservation() throws AmbiguousResultsFoundException, UnrecognizedCodeException, AnnotationNotFoundException {
        if (annotationMap.get(loincId) == null) throw new AnnotationNotFoundException();
        Set<HpoTerm4TestOutcome> results = new HashSet<>();
        codedValue.getCoding()
                .stream()
                .filter(p -> annotationMap.get(loincId).getCodes().contains(new Code(p)))
                .forEach(p -> results.add(annotationMap.get(loincId).loincInterpretationToHPO(new Code(p))));
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

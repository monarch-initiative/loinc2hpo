package org.monarchinitiative.loinc2hpocore.fhir;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.loinc2hpocore.Constants;
import org.monarchinitiative.loinc2hpocore.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpocore.exception.*;
import org.monarchinitiative.loinc2hpocore.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpocore.testresult.BasicLabTestOutcome;
import org.monarchinitiative.loinc2hpocore.testresult.LabTestOutcome;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is responsible for analyzing a FHIR observation
 */
public class FhirObservationAnalyzer {
    private static final Logger logger = LogManager.getLogger();

    static private Observation observation;
    static private Set<LoincId> loincIds;
    static Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap;
    static CodeSystemConvertor codeSystemConvertor;

    /**
     * Initialize the resources required for observation to hpo transformation
     * @param loincIdsSet
     * @param loincAnnotationMap
     */
    public static void init(Set<LoincId> loincIdsSet, Map<LoincId, LOINC2HpoAnnotationImpl> loincAnnotationMap) {
        loincIds = loincIdsSet;
        annotationMap = loincAnnotationMap;
        codeSystemConvertor = new CodeSystemConvertor();
    }

    public static void setObservation(Observation aFhirObservation) {
        observation = aFhirObservation;
    }
    public static Observation getObservation(){ return observation; }

    public static LabTestOutcome getHPO4ObservationOutcome(Observation observationToAnalyze) throws FHIRException, ReferenceNotFoundException, LoincCodeNotAnnotatedException, AmbiguousResultsFoundException, UnrecognizedCodeException, LoincCodeNotFoundException, MalformedLoincCodeException, AnnotationNotFoundException, UnsupportedCodingSystemException, AmbiguousReferenceException {
        observation = observationToAnalyze;
        return getHPO4ObservationOutcome(loincIds, annotationMap);
    }

    public static LabTestOutcome getHPO4ObservationOutcome(Observation.ObservationComponentComponent component) {
        component.getCode();
        return null;
    }

    /**
     * This method is to analyze a ObservationRelatedComponenent that is usually a component of a LOINC panel.
     * @param relatedComponent
     * @return
     */
    public static LabTestOutcome getHPO4ObservationOutcome(Observation.ObservationRelatedComponent relatedComponent) {

        return null;
    }

    /**
     * A core function that tries three ways to return a LabTestOutcome object:
     * first, it tries to return the result through the interpretation field. If it fails,
     * second, it tries to return the result through the quantative value, or
     * third, it tries to return the retult through the coded value (ordinal Loinc)
     * @param loinc2HPOannotationMap
     * @param loincIds
     * @return
     */
    public static LabTestOutcome getHPO4ObservationOutcome(Set<LoincId> loincIds, Map<LoincId, LOINC2HpoAnnotationImpl> loinc2HPOannotationMap) throws MalformedLoincCodeException, UnsupportedCodingSystemException, LoincCodeNotFoundException, AmbiguousResultsFoundException, AnnotationNotFoundException, UnrecognizedCodeException, LoincCodeNotAnnotatedException, AmbiguousReferenceException, ReferenceNotFoundException, FHIRException {

        //first make sure the observation has a valid loinc code; otherwise, we cannot handle it
        if (!hasValidLoincCode(loincIds)) {
            //TODO: consider handling this as a future project
            throw new LoincCodeNotFoundException();
        }

        LoincId loincId = getLoincIdOfObservation();

        if (!loinc2HPOannotationMap.containsKey(loincId)) {
            throw new LoincCodeNotAnnotatedException();
        }

        HpoTerm4TestOutcome hpoterm = null;
        if (observation.hasInterpretation()) {
            logger.debug("enter analyzer using the interpretation field");
            try {
                //hpoterm won't be null
                hpoterm =
                        new ObservationAnalysisFromInterpretation(getLoincIdOfObservation(), observation.getInterpretation(), loinc2HPOannotationMap, codeSystemConvertor).getHPOforObservation();
                return new BasicLabTestOutcome(hpoterm, null, observation.getSubject(), observation.getIdentifier());
            } catch (AnnotationNotFoundException e) {
                logger.trace("Annotation for the interpretation code is not found; try other methods");
            } //catch (AmbiguousResultsFoundException e) {  //we should not catch this exception, I think --@azhang
              //  logger.trace("Interpretation code resulted conflicting hpo interpretation; try other methods");
            //}
        }

        //if we failed to analyze the outcome through the interpretation field, we try to analyze the raw value using
        //the reference range
        //Qn will have a value field
        if (observation.hasValueQuantity()) {
            hpoterm = new ObservationAnalysisFromQnValue(loincId, observation, loinc2HPOannotationMap).getHPOforObservation();
            if (hpoterm != null) return new BasicLabTestOutcome(hpoterm, null, observation.getSubject(), observation.getIdentifier());
        }

        //Ord will have a ValueCodeableConcept field
        if (observation.hasValueCodeableConcept()) {
            hpoterm = new ObservationAnalysisFromCodedValues(loincId,
                    observation.getValueCodeableConcept(), loinc2HPOannotationMap).getHPOforObservation();
            if (hpoterm != null) {
                return new BasicLabTestOutcome(hpoterm, null, observation.getSubject(), observation.getIdentifier());
            }
        }

        //@TODO: analyze observations with

        //if all the above fails, we cannot do nothing
        logger.error("Could not return HPO for observation: " + observation.getId());
        return new BasicLabTestOutcome(null, null, observation.getSubject(), observation.getIdentifier());
    }

    /**
     * Check whether a FHIR observation has a valid Loinc code
     * @param loincIds: a hashset of all loinc codes (just codes)
     * @return false if the observation does not have one, or in wrong format, or recognized in the hashset
     */
    private static boolean hasValidLoincCode(Set<LoincId> loincIds){

        for (Coding coding : observation.getCode().getCoding()) {
            if (coding.getSystem().equals("http://loinc.org")) {
                try {
                    LoincId loincId = new LoincId(coding.getCode());
                    if (!loincIds.contains(loincId)) {
                        logger.info("The observation has a correctly formed loinc code, but the code is not found in the loinc table. Check whether it is a new loinc code: " + loincId);
                    }
                    return loincIds.contains(loincId);
                } catch (MalformedLoincCodeException e) {
                    logger.error("The loinc code is not formed correctly: " + coding.getCode());
                    return false;
                }
            }
        }
        logger.info("The observation does not have a loinc code. Observation ID: " + observation.getId());
        return false;
    }


    /**
     * A method to get the loinc id from a FHIR observation
     * @return
     * @throws MalformedLoincCodeException
     * @throws LoincCodeNotFoundException
     * @throws UnsupportedCodingSystemException
     */
    public static LoincId getLoincIdOfObservation() throws MalformedLoincCodeException, LoincCodeNotFoundException,
            UnsupportedCodingSystemException {
        LoincId loincId = null;
        for (Coding coding : observation.getCode().getCoding()) {
            if (coding.getSystem().equals("http://loinc.org")) {
                loincId = new LoincId(coding.getCode());
            }
        }
        if (loincId == null) throw new LoincCodeNotFoundException();
        return loincId;
    }

    /**
     * Extract LOINC code strings from the observation.
     * Note: the function does not check the returned value conform LoincId format
     * @param observation
     * @return a list of LOINC codes
     */
    public static List<String> getLoincIdOfObservation(Observation observation) {
        return observation.getCode().getCoding().stream()
                .filter(c -> c.getSystem().equals(Constants.LOINCSYSTEM))
                .map(Coding::getCode)
                .collect(Collectors.toList());
    }

    /**
     * Convert a FHIR observation to HPO through the interpretation field
     * @param interpretation
     * @param testmap
     * @return
     * @throws MalformedLoincCodeException
     * @throws LoincCodeNotFoundException
     * @throws UnsupportedCodingSystemException
     */
    /**
    public static LabTestOutcome getHPOFromInterpretation (
            CodeableConcept interpretation, Map<LoincId, LOINC2HpoAnnotationImpl> testmap) throws MalformedLoincCodeException,
            LoincCodeNotFoundException, UnsupportedCodingSystemException, AnnotationNotFoundException  {
        //here we only look at interpretation code system defined by HL7
        Code interpretationCode = null;
        for (Coding coding : interpretation.getCoding()) {
            if (coding.getSystem().equals("http://hl7.org/fhir/v2/0078")) {
                interpretationCode = Code.getNewCode()
                        .setSystem(coding.getSystem())
                        .setCode(coding.getCode());
                break;
            }
        }
        if (interpretationCode != null) {
            //find result
            try {
                Code internalCode = CodeSystemConvertor.convertToInternalCode(interpretationCode);
                LoincId loincId = getLoincIdOfObservation(); //get the loinc code from the observation
                LOINC2HpoAnnotationImpl annotationForLoinc = testmap.get(loincId); //get the annotation class for this loinc code
                if(annotationForLoinc == null) throw new AnnotationNotFoundException();
                HpoTerm4TestOutcome hpoId = annotationForLoinc.loincInterpretationToHPO(internalCode);
                return new BasicLabTestOutcome(hpoId, null);

            } catch (InternalCodeNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            logger.error("Could not recognize the coding system for interpretation");
            for (Coding coding : interpretation.getCoding()) {
                logger.error("coding system: " + coding.getSystem() + " Value: " + coding.getCode());
            }
            throw new UnsupportedCodingSystemException("Coding system is not supported");
        }
        return null;
    }
     **/


    /**
     * Convert the observation to HPO through the measured raw value (quantitative type)
     * need to have a Qn value AND a reference range
     * @return
     */
    public static LabTestOutcome getHPOFromRawValue(){

        return null;
    }

    /**
     * Convert the observation to HPO through the measured raw value (ordinal type)
     * need to have an ordinal value
     * @return
     */
    public static LabTestOutcome getHPOFromCodedValue() throws FHIRException {

        return null;
    }

}

package org.monarchinitiative.loinc2hpo.fhir;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.loinc2hpo.exception.AnnotationNotFoundException;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.exception.LoincCodeNotFoundException;
import org.monarchinitiative.loinc2hpo.exception.UnsupportedCodingSystemException;
import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;
import org.monarchinitiative.loinc2hpo.loinc.Loinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.ObservationResultInInternalCode;
import org.monarchinitiative.loinc2hpo.testresult.BasicLabTestResultInHPO;
import org.monarchinitiative.loinc2hpo.testresult.LabTestResultInHPO;

import java.util.HashSet;
import java.util.Map;

/**
 * This class is responsible for analyzing a FHIR observation
 */
public class FhirObservationAnalyzer {
    private static final Logger logger = LogManager.getLogger();

    static private Observation observation;

    public static void setObservation(Observation aFhirObservation) {
        observation = aFhirObservation;
    }
    public static Observation getObservation(){ return observation; }


    /**
     * A core function that tries three ways to return a LabTestResultInHPO object:
     * first, it tries to return the result through the interpretation field. If it fails,
     * second, it tries to return the result through the quantative value, or
     * third, it tries to return the retult through the coded value (ordinal Loinc)
     * @param loinc2HPOannotationMap
     * @param loincIds
     * @return
     */
    public static LabTestResultInHPO getHPO4ObservationOutcome(HashSet<LoincId> loincIds, Map<LoincId, Loinc2HPOAnnotation> loinc2HPOannotationMap) {
        //first make sure the observation has a valid loinc code; otherwise, we cannot handle it
        if (!hasValidLoincCode(loincIds)) {
            //TODO: consider handling this as a future project
            return null;
        }
        if (observation.hasInterpretation()) {
            try {
                return getHPOFromInterpretation(observation.getInterpretation(), loinc2HPOannotationMap);
            } catch (MalformedLoincCodeException e1) {
                //not going to happen
                logger.error("malformed loinc code.");
            } catch (LoincCodeNotFoundException e2) {
                //not going to happen
                logger.error("no loinc code is found in the observation");
            } catch (UnsupportedCodingSystemException e3) {
                //not going to happen
                logger.error("The interpretation coding system cannot be recognized.");
            } catch (AnnotationNotFoundException e4){
                logger.error("Annotation is not found");
            }
        }

        //if we failed to analyze the outcome through the interpretation field, we try to analyze the raw value using
        //the reference range
        //Qn will have a value field
        if (observation.hasValue()) {
            return getHPOFromRawValue();
        }

        //Ord will have a ValueCodeableConcept field
        if (observation.hasValueCodeableConcept()) {
            try {
                return getHPOFromCodedValue();
            } catch (FHIRException e) {
                //not going to happen
                logger.error("Could not get HPO term from coded value");
            }
        }

        //if all the above fails, we cannot do nothing
        logger.error("Could not return HPO for observation: " + observation.getId());
        return null;
    }

    /**
     * Check whether a FHIR observation has a valid Loinc code
     * @param loincIds: a hashset of all loinc codes (just codes)
     * @return false if the observation does not have one, or in wrong format, or recognized in the hashset
     */
    private static boolean hasValidLoincCode(HashSet<LoincId> loincIds){

        for (Coding coding : observation.getCode().getCoding()) {
            if (coding.getSystem().equals("http://loinc.org")) {
                try {
                    LoincId loincId = new LoincId(coding.getCode());
                    if (!loincIds.contains(loincId)) {
                        logger.info("The observation has a correctly formed loinc code, but the code is not found in the loinc table. Check whether it is a new loinc code");
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
     * Convert a FHIR observation to HPO through the interpretation field
     * @param interpretation
     * @param testmap
     * @return
     * @throws MalformedLoincCodeException
     * @throws LoincCodeNotFoundException
     * @throws UnsupportedCodingSystemException
     */
    public static LabTestResultInHPO getHPOFromInterpretation (
            CodeableConcept interpretation, Map<LoincId, Loinc2HPOAnnotation> testmap) throws MalformedLoincCodeException,
            LoincCodeNotFoundException, UnsupportedCodingSystemException, AnnotationNotFoundException  {
        //here we only look at interpretation code system defined by HL7
        String interpretationCode = null;
        for (Coding coding : interpretation.getCoding()) {
            if (coding.getSystem().equals("http://hl7.org/fhir/v2/0078")) {
                interpretationCode = coding.getCode();
                break;
            }
        }
        if (interpretationCode == null) {
            logger.error("Could not recognize the coding system for interpretation");
            for (Coding coding : interpretation.getCoding()) {
                logger.error("coding system: " + coding.getSystem() + " Value: " + coding.getCode());
            }
            throw new UnsupportedCodingSystemException("Coding system is not supported");
        } else {
            //TODO: create a factory that convert external coding system to internal coding
            ObservationResultInInternalCode observationResult = new ObservationResultInInternalCode(interpretationCode);
            LoincId loincId = getLoincIdOfObservation(); //get the loinc code from the observation
            Loinc2HPOAnnotation annotationForLoinc = testmap.get(loincId); //get the annotation class for this loinc code
            if(annotationForLoinc == null) throw new AnnotationNotFoundException();
            HpoTermId4LoincTest hpoId = annotationForLoinc.loincInterpretationToHpo(observationResult);
            return new BasicLabTestResultInHPO(hpoId, observationResult, "?");
        }
    }

    /**
     * Convert the observation to HPO through the measured raw value (quantitative type)
     * need to have a Qn value AND a reference range
     * @return
     */
    public static LabTestResultInHPO getHPOFromRawValue(){

        return null;
    }

    /**
     * Convert the observation to HPO through the measured raw value (ordinal type)
     * need to have an ordinal value
     * @return
     */
    public static LabTestResultInHPO getHPOFromCodedValue() throws FHIRException {

        return null;
    }

}

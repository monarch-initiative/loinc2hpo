package org.monarchinitiative.loinc2hpo.fhir;

import javassist.CodeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.exception.*;
import org.monarchinitiative.loinc2hpo.loinc.*;
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
        LoincId loincId = null;
        try {
            loincId = getLoincIdOfObservation();
        } catch (MalformedLoincCodeException e) {
            logger.error("malformed loinc code; should never happen");
            return null;
        } catch (LoincCodeNotFoundException e) {
            logger.error("No loinc code was found in the observation; should never happen");
            return null;
        } catch (UnsupportedCodingSystemException e) {
            logger.error("coding system not recognized");
            return null;
        }


        if (!loinc2HPOannotationMap.containsKey(loincId)) {
            return null;
        }



        if (observation.hasInterpretation()) {
            logger.debug("enter analyzer using the interpretation field");
            try {
                //return getHPOFromInterpretation(observation.getInterpretation(), loinc2HPOannotationMap);
                HpoTermId4LoincTest hpoterm = new ObservationAnalysisFromInterpretation(getLoincIdOfObservation(), observation.getInterpretation(), loinc2HPOannotationMap).getHPOforObservation();
                return new BasicLabTestResultInHPO(hpoterm, null);
            } catch (UnrecognizedCodeException e) {
                //this means the interpretation code is not recognized
                logger.info("The interpretation codes for this loinc code is not annotated; system will try using raw values");
            } catch (MalformedLoincCodeException e1) {
                //not going to happen
                logger.error("malformed loinc code.");
                return null;
            } catch (LoincCodeNotFoundException e2) {
                //not going to happen
                logger.error("no loinc code is found in the observation");
                return null;
            } catch (UnsupportedCodingSystemException e3) {
                //not going to happen
                logger.error("The interpretation coding system cannot be recognized.");
                return null;
            } catch (AmbiguousResultsFoundException e) {
                logger.error("The observation has conflicting interpretation codes.");
                return null;
            } catch (AnnotationNotFoundException e) {
                logger.error("There is no annotation for the loinc code used in the observation");
            }
        }

        //if we failed to analyze the outcome through the interpretation field, we try to analyze the raw value using
        //the reference range
        //Qn will have a value field
        if (observation.hasValueQuantity()) {
            try {
                HpoTermId4LoincTest hpoterm = new ObservationAnalysisFromQnValue(loincId, observation, loinc2HPOannotationMap).getHPOforObservation();
                if (hpoterm != null) return new BasicLabTestResultInHPO(hpoterm, null);
            } catch (ReferenceNotFoundException e) {
                //if there is no reference
                logger.error("The observation has no reference field.");
                //TODO: make a list of our own references
            } catch (AmbiguousReferenceException e) {
                logger.info("There are two reference ranges or more");
            } catch (UnrecognizedCodeException e) {
                logger.error("uncognized coding system");
            }

        }

        //Ord will have a ValueCodeableConcept field
        if (observation.hasValueCodeableConcept()) {
            try {
                HpoTermId4LoincTest hpoterm = null;
                hpoterm = new ObservationAnalysisFromCodedValues(loincId,
                        observation.getValueCodeableConcept(), loinc2HPOannotationMap).getHPOforObservation();
                if (hpoterm != null) return new BasicLabTestResultInHPO(hpoterm, null);
            } catch (AmbiguousResultsFoundException e) {
                logger.error("multiple results are found");
            } catch (UnrecognizedCodeException e) {
                logger.error("unrecognized codes");
            } catch (FHIRException e) {
                //not going to happen
                logger.error("Could not get HPO term from coded value");
            } catch (AnnotationNotFoundException e) {
                logger.error("There is no annotation for the loinc code used in the observation");
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
                Loinc2HPOAnnotation annotationForLoinc = testmap.get(loincId); //get the annotation class for this loinc code
                if(annotationForLoinc == null) throw new AnnotationNotFoundException();
                HpoTermId4LoincTest hpoId = annotationForLoinc.loincInterpretationToHPO(internalCode);
                return new BasicLabTestResultInHPO(hpoId, null, "?");

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

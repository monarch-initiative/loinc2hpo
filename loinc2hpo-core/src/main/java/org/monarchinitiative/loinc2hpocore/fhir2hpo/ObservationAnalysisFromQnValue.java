package org.monarchinitiative.loinc2hpocore.fhir2hpo;

import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.exception.*;
import org.monarchinitiative.loinc2hpocore.util.AgeCalculator;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.codesystems.InternalCode;
import org.monarchinitiative.loinc2hpocore.codesystems.InternalCodeSystem;
import org.monarchinitiative.loinc2hpocore.annotationmodel.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.annotationmodel.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ObservationAnalysisFromQnValue implements ObservationAnalysis {

//    private Reference subject; //we need subject because the applicable reference may dependent on sex, age ... of subject
//    private Quantity valueQuantity;
//    private List<Observation.ObservationReferenceRangeComponent> references; // a list of references
//    private Patient patient;
//    private Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap;
//    private LoincId loincId;

    private Observation observation;
    private Loinc2Hpo loinc2Hpo;


    public ObservationAnalysisFromQnValue(Loinc2Hpo loinc2Hpo, Observation observation){
        this.loinc2Hpo = loinc2Hpo;
        this.observation = observation;
    }

//    public ObservationAnalysisFromQnValue(LoincId loincId, Observation observation, Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap){
//        this.loincId = loincId;
//        this.observation = observation;
//        this.annotationMap = annotationMap;
//
//        this.subject = observation.getSubject();
//        try {
//            this.valueQuantity = observation.getValueQuantity();
//        } catch (FHIRException e) {
//            e.printStackTrace();
//        }
//        this.references = observation.getReferenceRange();
//    }

//    private Patient getPatient() throws AmbiguousSubjectException, SubjectNotFoundException {
//        Patient patient = FhirResourceRetriever.retrievePatientFromServer(subject);
//        return patient;
//    }

//    /**
//     * Get the age of the patient when the observation was made
//     * @return
//     */
//    private Age getPatientAge() throws AmbiguousSubjectException, SubjectNotFoundException, FHIRException {
//        if (this.patient == null) this.patient = getPatient();
//        Date patientBirth = this.patient.getBirthDate();
//        Date testDate = null;
//        if (observation.hasEffective()) {
//            //is this going to happen? "Effective" should either be effectiveDateTime or effectivePeriod, which are
//            //handled below
//        } else if (observation.hasEffectiveDateTimeType()) {
//            testDate = observation.getEffectiveDateTimeType().getValue();
//        } else if (observation.hasEffectivePeriod()) {
//            testDate = observation.getEffectivePeriod().getStart();
//        } else {
//            return null;
//        }
//        LocalDate birth = AgeCalculator.toLocalDate(patientBirth);
//        LocalDate current = AgeCalculator.toLocalDate(testDate);
//        int ageInDays = AgeCalculator.calculateAgeInDays(birth, current);
//        return new Age(); //TODO: complete this
//    }

//    /**
//     * Assess whether patient age is within range. Pay attention to the unit. Maybe normalize everything to years.
//     * @param component
//     * @return
//     */
//    //TODO: complete this method
//    private boolean withinAgeRange(Observation.ObservationReferenceRangeComponent component){
//        /**
//        BigDecimal low = component.getAge().getLow().getValue();
//        BigDecimal high = component.getAge().getHigh().getValue();
//        String unitReference = component.getAge().getLow().getUnit();
//
//        try {
//            Age patientAge = getPatientAge();
//        } catch (FHIRException e) {
//            e.printStackTrace();
//        } catch (AmbiguousSubjectException e) {
//            e.printStackTrace();
//        } catch (SubjectNotFoundException e) {
//            e.printStackTrace();
//        }
//        //if (low < patientAge < high) return true;
//         **/
//        return true;
//    }
//
//    private boolean sexMatch(Observation.ObservationReferenceRangeComponent component) {
//
//        return true;
//    }


    @Override
    public HpoTerm4TestOutcome getHPOforObservation() throws ReferenceNotFoundException, AmbiguousReferenceException, UnrecognizedCodeException, LoincCodeNotAnnotatedException, AnnotationNotFoundException, LoincCodeNotFoundException, MalformedLoincCodeException {

        LoincId loincId =
                FhirObservationUtil.getLoincIdOfObservation(this.observation);

        HpoTerm4TestOutcome hpoTerm4TestOutcome = null;
        //find applicable reference range
        List<Observation.ObservationReferenceRangeComponent> references =
                this.observation.getReferenceRange();

        if (references.size() == 0) {
            throw new ReferenceNotFoundException();
        }

        if (references.size() >= 2){
            throw new AmbiguousReferenceException();
            //An exception: three reference sizes
            //it can happen when there is actually one range but coded in three ranges
            //e.g. normal 20-30
            //in this case, one range ([20, 30]) is sufficient;
            //however, it is written as three ranges: ( , 20) [20, 30] (30, )
            //We should handle this case
        }

        Observation.ObservationReferenceRangeComponent targetReference = references.get(0);
        double low = targetReference.hasLow()? targetReference.getLow().getValue().doubleValue() : Double.MIN_VALUE;
        double high = targetReference.hasHigh() ? targetReference.getHigh().getValue().doubleValue() : Double.MAX_VALUE;
        double observed =
                this.observation.getValueQuantity().getValue().doubleValue();
        InternalCode internalCode;
        if (observed < low) {
            internalCode = InternalCode.fromCode("L");
        } else if (observed > high) {
            internalCode = InternalCode.fromCode("H");
        } else {
            internalCode = InternalCode.fromCode("N");
        }

        hpoTerm4TestOutcome = loinc2Hpo.query(loincId,
                InternalCodeSystem.getCode(internalCode));

        return hpoTerm4TestOutcome;
    }
}

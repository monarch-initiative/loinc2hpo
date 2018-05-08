package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.exception.*;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.util.AgeCalculator;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ObservationAnalysisFromQnValue implements ObservationAnalysis {

    private Reference subject; //we need subject because the applicable reference may dependent on sex, age ... of subject
    private Quantity valueQuantity;
    private List<Observation.ObservationReferenceRangeComponent> references; // a list of references
    private Patient patient;

    private Observation observation;
    private Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap;
    private LoincId loincId;

    public ObservationAnalysisFromQnValue(LoincId loincId, Observation observation, Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap){
        this.loincId = loincId;
        this.observation = observation;
        this.annotationMap = annotationMap;

        this.subject = observation.getSubject();
        try {
            this.valueQuantity = observation.getValueQuantity();
        } catch (FHIRException e) {
            e.printStackTrace();
        }
        this.references = observation.getReferenceRange();
    }

    private Patient getPatient() throws AmbiguousSubjectException, SubjectNotFoundException {
        Patient patient = FhirResourceRetriever.retrievePatientFromServer(subject);
        return patient;
    }

    /**
     * Get the age of the patient when the observation was made
     * @return
     */
    private Age getPatientAge() throws AmbiguousSubjectException, SubjectNotFoundException, FHIRException {
        if (this.patient == null) this.patient = getPatient();
        Date patientBirth = this.patient.getBirthDate();
        Date testDate = null;
        if (observation.hasEffective()) {
            //is this going to happen? "Effective" should either be effectiveDateTime or effectivePeriod, which are
            //handled below
        } else if (observation.hasEffectiveDateTimeType()) {
            testDate = observation.getEffectiveDateTimeType().getValue();
        } else if (observation.hasEffectivePeriod()) {
            testDate = observation.getEffectivePeriod().getStart();
        } else {
            return null;
        }
        LocalDate birth = AgeCalculator.toLocalDate(patientBirth);
        LocalDate current = AgeCalculator.toLocalDate(testDate);
        int ageInDays = AgeCalculator.calculateAgeInDays(birth, current);
        return new Age(); //TODO: complete this
    }

    /**
     * Assess whether patient age is within range. Pay attention to the unit. Maybe normalize everything to years.
     * @param component
     * @return
     */
    //TODO: complete this method
    private boolean withinAgeRange(Observation.ObservationReferenceRangeComponent component){
        /**
        BigDecimal low = component.getAge().getLow().getValue();
        BigDecimal high = component.getAge().getHigh().getValue();
        String unitReference = component.getAge().getLow().getUnit();

        try {
            Age patientAge = getPatientAge();
        } catch (FHIRException e) {
            e.printStackTrace();
        } catch (AmbiguousSubjectException e) {
            e.printStackTrace();
        } catch (SubjectNotFoundException e) {
            e.printStackTrace();
        }
        //if (low < patientAge < high) return true;
         **/
        return true;
    }

    private boolean sexMatch(Observation.ObservationReferenceRangeComponent component) {

        return true;
    }


    @Override
    public HpoTerm4TestOutcome getHPOforObservation() throws ReferenceNotFoundException, AmbiguousReferenceException, UnrecognizedCodeException {

        HpoTerm4TestOutcome hpoTerm4TestOutcome = null;
        //find applicable reference range
        List<Observation.ObservationReferenceRangeComponent> references =
                this.references.stream()
                .filter(p -> withinAgeRange(p))
                .collect(Collectors.toList());

        if (references.size() < 1) {
            throw new ReferenceNotFoundException();
        } else if (references.size() == 1) {
            Observation.ObservationReferenceRangeComponent targetReference = references.get(0);
            double low = targetReference.hasLow()? targetReference.getLow().getValue().doubleValue() : Double.MIN_VALUE;
            double high = targetReference.hasHigh() ? targetReference.getHigh().getValue().doubleValue() : Double.MAX_VALUE;
            double observed = valueQuantity.getValue().doubleValue();
            Loinc2HPOCodedValue result;
            if (observed < low) {
                result = Loinc2HPOCodedValue.fromCode("L");
            } else if (observed > high) {
                result = Loinc2HPOCodedValue.fromCode("H");
            } else {
                result = Loinc2HPOCodedValue.fromCode("N");
            }
            Code resultCode = Code.getNewCode().setSystem(Loinc2HPOCodedValue.CODESYSTEM).setCode(result.toCode());
            hpoTerm4TestOutcome = annotationMap.get(loincId).loincInterpretationToHPO(resultCode);
        } else if (references.size() == 2) {
            //what does it mean with multiple references
            throw new AmbiguousReferenceException();
        } else if (references.size() == 3){
            //it can happen when there is actually one range but coded in three ranges
            //e.g. normal 20-30
            //in this case, one range ([20, 30]) is sufficient;
            //however, it is written as three ranges: ( , 20) [20, 30] (30, )
            //We should handle this case


            throw new AmbiguousReferenceException();

        } else {
            throw new AmbiguousReferenceException();
        }
        //if we can still not find an answer, it is probably that we did not have the annotation
        if (hpoTerm4TestOutcome == null) throw new UnrecognizedCodeException();
        return hpoTerm4TestOutcome;
    }
}

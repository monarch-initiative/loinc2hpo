package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.exceptions.FHIRException;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.exception.AmbiguousReferenceException;
import org.monarchinitiative.loinc2hpo.exception.ReferenceNotFoundException;
import org.monarchinitiative.loinc2hpo.exception.UnrecognizedCodeException;
import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;
import org.monarchinitiative.loinc2hpo.loinc.Loinc2HPOAnnotation;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ObservationAnalysisFromQnValue implements ObservationAnalysis {

    private Reference subject; //we need subject because the applicable reference may dependent on sex, age ... of subject
    private Quantity valueQuantity;
    private List<Observation.ObservationReferenceRangeComponent> references; // a list of references

    private Observation observation;
    private Map<LoincId, Loinc2HPOAnnotation> annotationMap;
    private LoincId loincId;

    public ObservationAnalysisFromQnValue(LoincId loincId, Observation observation, Map<LoincId, Loinc2HPOAnnotation> annotationMap){
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

    /**
     * Get the age of the patient when the observation was made
     * @return
     */
    private Age getPatientAge(){
        Date patientBirth = FhirResourceRetriever.retrievePatientFromServer(subject).getBirthDate();
        Date testDate;
        if (observation.hasEffective()) {
            //testDate = observation.getEffective().castToDate(Base.class);

        } else if (observation.hasEffectiveDateTimeType()) {

        } else if (observation.hasEffectivePeriod()) {

        } else {
            return null;
        }
        return new Age();
    }

    /**
     * Assess whether patient age is within range. Pay attention to the unit. Maybe normalize everything to years.
     * @param component
     * @return
     */
    private boolean withinAgeRange(Observation.ObservationReferenceRangeComponent component){
        BigDecimal low = component.getAge().getLow().getValue();
        BigDecimal high = component.getAge().getHigh().getValue();
        String unitReference = component.getAge().getLow().getUnit();

        Age patientAge = getPatientAge();
        //if (low < patientAge < high) return true;
        return true;
    }


    @Override
    public HpoTermId4LoincTest getHPOforObservation() throws ReferenceNotFoundException, AmbiguousReferenceException, UnrecognizedCodeException {
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
            return annotationMap.get(loincId).loincInterpretationToHPO(resultCode);
        } else if (references.size() == 2) {
            //what does it mean with multiple references
            throw new AmbiguousReferenceException();
        } else if (references.size() == 3){



        } else {
            throw new AmbiguousReferenceException();
        }
        return null;
    }
}

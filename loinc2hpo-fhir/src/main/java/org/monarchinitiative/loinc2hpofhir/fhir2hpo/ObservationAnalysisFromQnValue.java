package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.hl7.fhir.dstu3.model.*;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;

public class ObservationAnalysisFromQnValue  {

    private Observation observation;
    private Loinc2Hpo loinc2Hpo;


    public ObservationAnalysisFromQnValue(Loinc2Hpo loinc2Hpo, Observation observation){
        this.loinc2Hpo = loinc2Hpo;
        this.observation = observation;
    }
/*

    public Hpo2Outcome getHPOforObservation() {

        LoincId loincId =
                FhirObservationUtil.getLoincIdOfObservation(this.observation);
        TermId termLoincId = TermId.of("LNC",loincId.toString());

        Optional<Hpo2Outcome> hpoTerm4TestOutcome = null;
        //find applicable reference range
        List<Observation.ObservationReferenceRangeComponent> references =
                this.observation.getReferenceRange();

        if (references.size() == 0) {
            throw Loinc2HpoRuntimeException.referenceRangeNotFound();
        }

        if (references.size() >= 2){
            throw Loinc2HpoRuntimeException.ambiguousReferenceRange();
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
        Outcome internalCode;
        if (observed < low) {
            internalCode = Outcome.LOW();// ShortCode.fromShortCode("L");
        } else if (observed > high) {
            internalCode = Outcome.HIGH();// ShortCode.fromShortCode("H");
        } else {
            internalCode = Outcome.NORMAL();//ShortCode.fromShortCode("N");
        }
        hpoTerm4TestOutcome = loinc2Hpo.query(termLoincId, internalCode);
        if (hpoTerm4TestOutcome.isEmpty()) {
            throw new Loinc2HpoRuntimeException("TODO");
        }
        return hpoTerm4TestOutcome.get();
    }

 */
}

package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.hl7.fhir.dstu3.model.*;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.exception.*;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Hpo2Outcome;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.List;

public class ObservationAnalysisFromQnValue implements ObservationAnalysis {

    private Observation observation;
    private Loinc2Hpo loinc2Hpo;


    public ObservationAnalysisFromQnValue(Loinc2Hpo loinc2Hpo, Observation observation){
        this.loinc2Hpo = loinc2Hpo;
        this.observation = observation;
    }


    @Override
    public Hpo2Outcome getHPOforObservation() {

        LoincId loincId =
                FhirObservationUtil.getLoincIdOfObservation(this.observation);

        Hpo2Outcome hpoTerm4TestOutcome = null;
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
        ShortCode internalCode;
        if (observed < low) {
            internalCode = ShortCode.fromShortCode("L");
        } else if (observed > high) {
            internalCode = ShortCode.fromShortCode("H");
        } else {
            internalCode = ShortCode.fromShortCode("N");
        }
        hpoTerm4TestOutcome = loinc2Hpo.query(loincId, internalCode);

        return hpoTerm4TestOutcome;
    }
}

package org.monarchinitiative.loinc2hpocore.fhir2hpo;

import org.hl7.fhir.dstu3.model.*;
import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.exception.*;
import org.monarchinitiative.loinc2hpocore.codesystems.InternalCode;
import org.monarchinitiative.loinc2hpocore.codesystems.InternalCodeSystem;
import org.monarchinitiative.loinc2hpocore.annotationmodel.HpoTerm4TestOutcome;
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

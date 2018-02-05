package org.monarchinitiative.loinc2hpo.fhir;

import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;

public class ObservationAnalysisFromQnValue implements ObservationAnalysis {
    @Override
    public HpoTermId4LoincTest getHPOforObservation() {
        return null;
    }
}

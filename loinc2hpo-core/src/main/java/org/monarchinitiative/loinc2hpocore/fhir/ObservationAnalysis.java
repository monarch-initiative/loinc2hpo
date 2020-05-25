package org.monarchinitiative.loinc2hpocore.fhir;

import org.monarchinitiative.loinc2hpocore.loinc.HpoTerm4TestOutcome;

public interface ObservationAnalysis {

    HpoTerm4TestOutcome getHPOforObservation() throws Exception;

}

package org.monarchinitiative.loinc2hpo.fhir;

import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;

public interface ObservationAnalysis {

    HpoTerm4TestOutcome getHPOforObservation() throws Exception;

}

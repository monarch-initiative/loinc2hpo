package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.monarchinitiative.loinc2hpocore.annotationmodel.HpoTerm4TestOutcome;

public interface ObservationAnalysis {

    HpoTerm4TestOutcome getHPOforObservation() throws Exception;

}

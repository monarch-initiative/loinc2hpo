package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.monarchinitiative.loinc2hpocore.annotationmodel.Hpo2Outcome;

public interface ObservationAnalysis {

    Hpo2Outcome getHPOforObservation() throws Exception;

}

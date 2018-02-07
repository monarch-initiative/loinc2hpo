package org.monarchinitiative.loinc2hpo.fhir;

import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;

public interface ObservationAnalysis {

    HpoTermId4LoincTest getHPOforObservation() throws Exception;

}

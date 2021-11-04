package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.Optional;

/**
 * This is a proxy for one of the versions of FHIR Observation (dtu3, r4, r5)
 */
public interface Uberobservation {

    Optional<LoincId> getLoincId();

    Optional<Outcome> getOutcome();

}

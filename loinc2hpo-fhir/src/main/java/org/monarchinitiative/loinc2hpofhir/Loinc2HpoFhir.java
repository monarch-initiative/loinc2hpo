package org.monarchinitiative.loinc2hpofhir;


import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.model.Hpo2Outcome;
import org.monarchinitiative.loinc2hpocore.model.Outcome;
import org.monarchinitiative.loinc2hpocore.model.LoincId;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.ObservationDtu3;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.ObservationR4;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.ObservationR5;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.Uberobservation;

import java.util.Optional;

/**
 * Retrieve LOINC2HPO results for FHIR versions dstu3, r4, and r5, using a proxy pattern.
 * We use HAPI FHIR to to get a {@link LoincId} and an {@link Outcome} from the FHIR Observation
 * and then use the {@link Loinc2Hpo} object to transform that into an HPO code. The method returns
 * an Optional which is empty if any of the steps fails.
 * @author Peter Robinson
 */
public class Loinc2HpoFhir {
    private final Loinc2Hpo loinc2Hpo;

    public Loinc2HpoFhir(String path) {
        this.loinc2Hpo = new Loinc2Hpo(path);
    }

    Optional<Hpo2Outcome> dstu3(org.hl7.fhir.dstu3.model.Observation dstu3Observation)  {
        Uberobservation observation = new ObservationDtu3(dstu3Observation);
       return query(observation);
    }

    Optional<Hpo2Outcome> r4(org.hl7.fhir.r4.model.Observation r4Observation) {
        Uberobservation observation = new ObservationR4(r4Observation);
        return query(observation);
    }

    Optional<Hpo2Outcome> r5(org.hl7.fhir.r5.model.Observation r5Observation) {
        Uberobservation observation = new ObservationR5(r5Observation);
        return query(observation);
    }


    Optional<Hpo2Outcome> query(Uberobservation uberobservation) {
        Optional<LoincId> loincOpt = uberobservation.getLoincId();
        if (loincOpt.isEmpty()) return Optional.empty();
        Optional<Outcome> outcomeOpt = uberobservation.getOutcome();
        if (outcomeOpt.isEmpty()) return Optional.empty();
        return loinc2Hpo.query(loincOpt.get(), outcomeOpt.get());
    }





}

package org.monarchinitiative.loinc2hpofhir;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;


import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Hpo2Outcome;
import org.monarchinitiative.loinc2hpocore.annotationmodel.LoincAnnotation;
import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;
import org.monarchinitiative.loinc2hpocore.io.Loinc2HpoAnnotationParser;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.ObservationDtu3;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.Uberobservation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;
import java.util.Optional;

public class Loin2HpoFhir {
    private final Loinc2Hpo loinc2Hpo;

    public Loin2HpoFhir(String path) {
        this.loinc2Hpo = new Loinc2Hpo(path);
    }

    Optional<Hpo2Outcome> dstu3(org.hl7.fhir.dstu3.model.Observation dstu3Observation)  {
        Uberobservation observation = new ObservationDtu3(dstu3Observation);
       return query(observation);
    }

    Optional<Hpo2Outcome> query(Uberobservation uberobservation) {
        Optional<LoincId> opt = uberobservation.getLoincId();

        return Optional.empty();
    }





}
package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Optional;

public interface LoincAnnotation {

    LoincId getLoincId();

    Optional<Hpo2Outcome> getOutcome(Outcome outcome);

    List<Loinc2HpoAnnotation> allAnnotations();

}

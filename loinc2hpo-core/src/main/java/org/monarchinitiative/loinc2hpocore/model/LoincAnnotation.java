package org.monarchinitiative.loinc2hpocore.model;

import java.util.List;
import java.util.Optional;

public interface LoincAnnotation {

    LoincId getLoincId();

    Optional<Hpo2Outcome> getOutcome(Outcome outcome);

    List<Loinc2HpoAnnotation> allAnnotations();

    LoincScale scale();

}

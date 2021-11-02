package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;

import java.util.Optional;

public interface LoincAnnotation {

    Optional<Hpo2Outcome> getAnnotation(Outcome outcome);


}

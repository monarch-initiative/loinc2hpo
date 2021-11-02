package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;
import java.util.Optional;

public class NominalLoincAnnotation implements LoincAnnotation {


    private final Map<Outcome, Loinc2HpoAnnotation> nominalAnnotations;

    public NominalLoincAnnotation(Map<Outcome, Loinc2HpoAnnotation> annotations) {
        this.nominalAnnotations = annotations;
    }


    @Override
    public Optional<Hpo2Outcome> getAnnotation(Outcome outcome) {
        if (nominalAnnotations.containsKey(outcome.getOutcome())) {
            TermId hpoId = nominalAnnotations.get(outcome.getOutcome()).getHpoTermId();
            return Optional.of(new Hpo2Outcome(hpoId, outcome.getCode()));
        } else {
            return Optional.empty();
        }
    }
}

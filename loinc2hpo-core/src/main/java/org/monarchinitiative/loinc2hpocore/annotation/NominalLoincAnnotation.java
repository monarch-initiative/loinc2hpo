package org.monarchinitiative.loinc2hpocore.annotation;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NominalLoincAnnotation implements LoincAnnotation {

    private final LoincId loincId;
    private final Map<Outcome, Loinc2HpoAnnotation> nominalAnnotations;

    public NominalLoincAnnotation(Map<Outcome, Loinc2HpoAnnotation> annotations) {
        this.nominalAnnotations = annotations;
        // The assumption is that annotations is not empty and that
        // each entry has the same LOINC id. This assumption will always
        // hold if the logic of the code is correct and the input data is correct
        Loinc2HpoAnnotation annot = annotations.values().iterator().next();
        this.loincId = annot.getLoincId();
    }


    @Override
    public LoincId getLoincId() {
        return this.loincId;
    }

    @Override
    public Optional<Hpo2Outcome> getOutcome(Outcome outcome) {
        if (nominalAnnotations.containsKey(outcome.getOutcome())) {
            TermId hpoId = nominalAnnotations.get(outcome.getOutcome()).getHpoTermId();
            return Optional.of(new Hpo2Outcome(hpoId, outcome.getCode()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Loinc2HpoAnnotation> allAnnotations() {
        return new ArrayList<>(this.nominalAnnotations.values());
    }
}

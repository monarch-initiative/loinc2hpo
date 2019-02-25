package org.monarchinitiative.loinc2hpo.util;

import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AnnotationQC {
    public static boolean hasUnrecognizedTermId(Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap, Ontology hpo) {
        for (LOINC2HpoAnnotationImpl annotation : annotationMap.values()) {
            boolean hasUnregnizedTerm = annotation.getCandidateHpoTerms().values().stream()
                    .map(HpoTerm4TestOutcome::getId)
                    .anyMatch(id -> !hpo.getTermMap().containsKey(id));
            if (hasUnregnizedTerm) {
                return true;
            }
        }
        return false;
    }

    public static String unrecognizedTermId(Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap, Ontology hpo) {
        List<Optional<String>> unrecognizedTerms = new ArrayList<>();
        for (LOINC2HpoAnnotationImpl annotation : annotationMap.values()) {
            Optional<String> unregnizedTerm = annotation.getCandidateHpoTerms().values().stream()
                    .map(HpoTerm4TestOutcome::getId)
                    .filter(id -> !hpo.getTermMap().containsKey(id))
                    .map(TermId::getValue)
                    .reduce((a,b)-> a + "\n" + b);
            unrecognizedTerms.add(unregnizedTerm);
        }
        return unrecognizedTerms.stream().filter(Optional::isPresent).map(Optional::get).reduce((r1, r2) -> r1 + "\n" + r2).orElse("");
    }
}

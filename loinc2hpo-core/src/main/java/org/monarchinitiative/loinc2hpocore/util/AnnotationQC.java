package org.monarchinitiative.loinc2hpocore.util;

import org.monarchinitiative.loinc2hpocore.annotationmodel.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.annotationmodel.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

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
        Set<TermId> unrecognizedTerms = new HashSet<>();
        for (LOINC2HpoAnnotationImpl annotation : annotationMap.values()) {
            annotation.getCandidateHpoTerms().values().stream()
                    .map(HpoTerm4TestOutcome::getId)
                    .filter(id -> !hpo.getTermMap().containsKey(id))
                    .forEach(unrecognizedTerms::add);
        }

        return unrecognizedTerms.stream().map(TermId::getValue).reduce((a, b) -> a + "\t" + b).orElse("");
    }
}

package org.monarchinitiative.loinc2hpo.util;

import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.util.Map;

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
}

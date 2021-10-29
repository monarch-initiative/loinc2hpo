package org.monarchinitiative.loinc2hpocore.legacy.util;

import org.monarchinitiative.loinc2hpocore.annotationmodel.Hpo2Outcome;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotationModelLEGACY;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

public class AnnotationQC {
    public static boolean hasUnrecognizedTermId(Map<LoincId, Loinc2HpoAnnotationModelLEGACY> annotationMap, Ontology hpo) {
        for (Loinc2HpoAnnotationModelLEGACY annotation : annotationMap.values()) {
            boolean hasUnregnizedTerm = annotation.getCandidateHpoTerms().values().stream()
                    .map(Hpo2Outcome::getId)
                    .anyMatch(id -> !hpo.getTermMap().containsKey(id));
            if (hasUnregnizedTerm) {
                return true;
            }
        }
        return false;
    }

    public static String unrecognizedTermId(Map<LoincId, Loinc2HpoAnnotationModelLEGACY> annotationMap, Ontology hpo) {
        Set<TermId> unrecognizedTerms = new HashSet<>();
        for (Loinc2HpoAnnotationModelLEGACY annotation : annotationMap.values()) {
            annotation.getCandidateHpoTerms().values().stream()
                    .map(Hpo2Outcome::getId)
                    .filter(id -> !hpo.getTermMap().containsKey(id))
                    .forEach(unrecognizedTerms::add);
        }

        return unrecognizedTerms.stream().map(TermId::getValue).reduce((a, b) -> a + "\t" + b).orElse("");
    }
}

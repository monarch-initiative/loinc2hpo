package org.monarchinitiative.loinc2hpocore;


import org.monarchinitiative.loinc2hpocore.annotationmodel.LoincAnnotation;
import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Hpo2Outcome;
import org.monarchinitiative.loinc2hpocore.io.Loinc2HpoAnnotationParser;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * Entry point for the Loinc2Hpo tool
 * @author <a href="mailto:aaron.zhang@sema4.com">Aaron Zhang</a>
 * @version 1.1.7
 */
public class Loinc2Hpo {
    private final Map<LoincId, LoincAnnotation> loincToHpoAnnotationMap;

    public Loinc2Hpo(String path){
        Loinc2HpoAnnotationParser parser = new Loinc2HpoAnnotationParser(path);
        loincToHpoAnnotationMap = parser.loincToHpoAnnotationMap();
    }

    public Optional<Hpo2Outcome> query(TermId loincId, Outcome outcome)  {
        if (! loincToHpoAnnotationMap.containsKey(loincId)) {
            return Optional.empty();
        } else {
            LoincAnnotation annot = loincToHpoAnnotationMap.get(loincId);
            return annot.getOutcome(outcome);
        }
    }


}

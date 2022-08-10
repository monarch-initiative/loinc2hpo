package org.monarchinitiative.loinc2hpocore;


import org.monarchinitiative.loinc2hpocore.model.LoincAnnotation;
import org.monarchinitiative.loinc2hpocore.model.Outcome;
import org.monarchinitiative.loinc2hpocore.model.Hpo2Outcome;
import org.monarchinitiative.loinc2hpocore.parser.Loinc2HpoAnnotationParser;
import org.monarchinitiative.loinc2hpocore.model.LoincId;

import java.util.Map;
import java.util.Optional;

/**
 * Entry point for the Loinc2Hpo tool
 * @author <a href="mailto:aaron.zhang@sema4.com">Aaron Zhang</a>
 * @version 1.6.0
 */
public class Loinc2Hpo {
    private final Map<LoincId, LoincAnnotation> loincToHpoAnnotationMap;

    public Loinc2Hpo(String path){
        Loinc2HpoAnnotationParser parser = new Loinc2HpoAnnotationParser(path);
        loincToHpoAnnotationMap = parser.loincToHpoAnnotationMap();
    }

    public Optional<Hpo2Outcome> query(LoincId loincId, Outcome outcome)  {
        if (! loincToHpoAnnotationMap.containsKey(loincId)) {
            return Optional.empty();
        } else {
            LoincAnnotation annot = loincToHpoAnnotationMap.get(loincId);
            return annot.getOutcome(outcome);
        }
    }


}

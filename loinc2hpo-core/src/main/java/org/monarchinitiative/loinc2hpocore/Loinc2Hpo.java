package org.monarchinitiative.loinc2hpocore;


import org.monarchinitiative.loinc2hpocore.annotation.LoincAnnotation;
import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.annotation.Hpo2Outcome;
import org.monarchinitiative.loinc2hpocore.io.Loinc2HpoAnnotationParser;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

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

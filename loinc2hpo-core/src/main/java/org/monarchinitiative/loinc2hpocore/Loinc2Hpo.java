package org.monarchinitiative.loinc2hpocore;


import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Hpo2Outcome;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotationModelLEGACY;
import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Entry point for the Loinc2Hpo tool
 * @author <a href="mailto:aaron.zhang@sema4.com">Aaron Zhang</a>
 * @version 1.1.7
 */
public class Loinc2Hpo {
    private static final Logger logger = LoggerFactory.getLogger(Loinc2Hpo.class);

    private final Map<LoincId, Loinc2HpoAnnotationModelLEGACY> annotationMap;

    public Loinc2Hpo(String path){
        try {
            annotationMap = Loinc2HpoAnnotationModelLEGACY.from_csv(path);
        } catch (Exception e) {
            logger.error("Failed to import loinc2hpo annotation");
            throw new RuntimeException("failed to import loinc2hpo annotation");
        }
    }

    public Map<LoincId, Loinc2HpoAnnotationModelLEGACY> getAnnotationMap() {
        return annotationMap;
    }


    public Hpo2Outcome query(LoincId loincId, ShortCode testResult)  {
        //The loinc id is not annotated yet
        if (!this.annotationMap.containsKey(loincId)) {
            throw Loinc2HpoRuntimeException.notAnnotated(loincId);
        }
        Loinc2HpoAnnotationModelLEGACY annotation = this.annotationMap.get(loincId);
        HashMap<ShortCode, Hpo2Outcome> annotations = annotation.getCandidateHpoTerms();

        //The result code is not annotated
        if (! annotations.containsKey(testResult)){
            throw Loinc2HpoRuntimeException.notAnnotated(loincId);
        }
        return annotations.get(testResult);
    }

    public Hpo2Outcome query(LoincId loincId, String system, String id) {
        ShortCode code = ShortCode.fromShortCode(id);
        return query(loincId, code);
    }


}

package org.monarchinitiative.loinc2hpocore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.exception.AnnotationNotFoundException;
import org.monarchinitiative.loinc2hpocore.exception.LoincCodeNotAnnotatedException;
import org.monarchinitiative.loinc2hpocore.io.LoincAnnotationSerializationFactory;
import org.monarchinitiative.loinc2hpocore.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.Map;

/**
 * Entry point for the Loinc2Hpo tool
 * @author <a href="mailto:aaron.zhang@jax.org">Aaron Zhang</a>
 * @version 1.1.7
 */
public class Loinc2Hpo {

    private static final Logger logger = LogManager.getLogger();

    private Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap;

    public Loinc2Hpo(String path){
        try {
            annotationMap = importAnnotationMap(path);
        } catch (Exception e) {
            logger.error("Failed to import loinc2hpo annotation");
            throw new RuntimeException("failed to import loinc2hpo annotation");
        }
    }

    private Map<LoincId, LOINC2HpoAnnotationImpl> importAnnotationMap(String path) throws Exception {
        annotationMap = LoincAnnotationSerializationFactory.parseFromFile(path, null, LoincAnnotationSerializationFactory.SerializationFormat.TSVSingleFile);
        return annotationMap;
    }


    public HpoTerm4TestOutcome query(LoincId loincId, Code testResult) throws AnnotationNotFoundException, LoincCodeNotAnnotatedException {

        //The loinc id is not annotated yet
        if (!this.annotationMap.containsKey(loincId)) {
            throw new LoincCodeNotAnnotatedException();
        }

        //The result code is not annotated
        if (!this.annotationMap.get(loincId).getCandidateHpoTerms().containsKey(testResult)){
            throw new AnnotationNotFoundException();
        }

        return this.annotationMap.get(loincId).getCandidateHpoTerms().get(testResult);
    }

    public HpoTerm4TestOutcome query(LoincId loincId, String system, String id) throws LoincCodeNotAnnotatedException, AnnotationNotFoundException {
        Code code = Code.getNewCode();
        code.setSystem(system).setCode(id);
        return query(loincId, code);
    }

}

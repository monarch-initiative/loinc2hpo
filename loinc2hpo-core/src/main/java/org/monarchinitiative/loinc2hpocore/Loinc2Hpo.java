package org.monarchinitiative.loinc2hpocore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpocore.exception.AnnotationNotFoundException;
import org.monarchinitiative.loinc2hpocore.exception.InternalCodeNotFoundException;
import org.monarchinitiative.loinc2hpocore.exception.LoincCodeNotAnnotatedException;
import org.monarchinitiative.loinc2hpocore.annotationmodel.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotationModel;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.Map;

/**
 * Entry point for the Loinc2Hpo tool
 * @author <a href="mailto:aaron.zhang@sema4.com">Aaron Zhang</a>
 * @version 1.1.7
 */
public class Loinc2Hpo {

    private static final Logger logger = LogManager.getLogger();

    private Map<LoincId, Loinc2HpoAnnotationModel> annotationMap;
    private CodeSystemConvertor converter;

    public Loinc2Hpo(Map<LoincId, Loinc2HpoAnnotationModel> annotationMap,
                     CodeSystemConvertor converter){
        this.annotationMap = annotationMap;
        this.converter = converter;
    }

    public Loinc2Hpo(String path, CodeSystemConvertor converter){
        try {
            annotationMap = Loinc2HpoAnnotationModel.from_csv(path);
        } catch (Exception e) {
            logger.error("Failed to import loinc2hpo annotation");
            throw new RuntimeException("failed to import loinc2hpo annotation");
        }
        this.converter = converter;
    }

    public Map<LoincId, Loinc2HpoAnnotationModel> getAnnotationMap() {
        return annotationMap;
    }

    public CodeSystemConvertor getConverter() {
        return converter;
    }

    public Code convertToInternal(Code original) throws InternalCodeNotFoundException {
        Code internal = this.converter.convertToInternalCode(original);
        return internal;
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

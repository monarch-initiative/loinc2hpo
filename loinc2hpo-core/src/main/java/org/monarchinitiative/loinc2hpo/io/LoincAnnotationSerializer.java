package org.monarchinitiative.loinc2hpo.io;

import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.util.Map;

public interface LoincAnnotationSerializer {

    void serialize(Map<LoincId, LOINC2HpoAnnotationImpl> annotationmap, String filepath) throws IOException;

    Map<LoincId, LOINC2HpoAnnotationImpl> parse(String filepath) throws Exception;

    default TermId convertToTermID(String record) {
        String prefix = "HP";
        if (!record.startsWith(prefix) || record.length() <= 3) {
            return null;
        }
        String id = record.substring(3);
        return TermId.of(prefix, id);
    }


}

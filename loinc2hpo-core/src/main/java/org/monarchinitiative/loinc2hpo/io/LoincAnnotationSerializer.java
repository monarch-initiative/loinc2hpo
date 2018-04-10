package org.monarchinitiative.loinc2hpo.io;

import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermPrefix;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public interface LoincAnnotationSerializer {

    void serialize(Map<LoincId, UniversalLoinc2HPOAnnotation> annotationmap, String filepath) throws IOException;

    Map<LoincId, UniversalLoinc2HPOAnnotation> parse(String filepath) throws Exception;

    default TermId convertToTermID(String record) {
        TermPrefix prefix = new ImmutableTermPrefix("HP");
        if (!record.startsWith(prefix.getValue()) || record.length() <= 3) {
            return null;
        }
        String id = record.substring(3);
        return new ImmutableTermId(prefix, id);
    }


}

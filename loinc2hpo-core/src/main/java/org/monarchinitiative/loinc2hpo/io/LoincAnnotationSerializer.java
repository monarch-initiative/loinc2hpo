package org.monarchinitiative.loinc2hpo.io;

import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;

import java.util.Map;

public interface LoincAnnotationSerializer {

    void serialize(Map<LoincId, UniversalLoinc2HPOAnnotation> annotationmap, String filepath) throws Exception;

    Map<LoincId, UniversalLoinc2HPOAnnotation> parse(String filepath) throws Exception;


}

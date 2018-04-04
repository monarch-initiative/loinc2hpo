package org.monarchinitiative.loinc2hpo.io;

import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;

import java.util.Map;

public class LoincAnnotationSerializerToTSVSeparateFiles implements LoincAnnotationSerializer {


    @Override
    public void serialize(Map<LoincId, UniversalLoinc2HPOAnnotation> annotationmap, String filepath) {

    }

    @Override
    public Map<LoincId, UniversalLoinc2HPOAnnotation> parse(String filepath) {
        return null;
    }
}

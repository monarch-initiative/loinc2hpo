package org.monarchinitiative.loinc2hpocore.io;

import org.monarchinitiative.loinc2hpocore.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.io.IOException;
import java.util.Map;

public interface LoincAnnotationSerializer {

    void serialize(Map<LoincId, LOINC2HpoAnnotationImpl> annotationmap, String filepath) throws IOException;

    Map<LoincId, LOINC2HpoAnnotationImpl> parse(String filepath) throws Exception;

}

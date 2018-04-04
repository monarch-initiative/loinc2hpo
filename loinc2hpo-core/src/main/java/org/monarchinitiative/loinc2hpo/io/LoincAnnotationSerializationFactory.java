package org.monarchinitiative.loinc2hpo.io;

import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;

import java.util.LinkedHashMap;
import java.util.Map;

public class LoincAnnotationSerializationFactory {

    public enum SerializationFormat {
        TSVSingleFile,
        TSVSeparateFile,
        JSON
    }

    public static void serializeToFile(Map<LoincId, UniversalLoinc2HPOAnnotation> annotationMap,
                                       SerializationFormat format) {
        switch (format) {
            case TSVSingleFile:
                break;
            case TSVSeparateFile:
                break;
            case JSON:
                break;
            default:
                break;

        }
    }

    public static Map<LoincId, UniversalLoinc2HPOAnnotation> parseFromFile(SerializationFormat format) {
        Map<LoincId, UniversalLoinc2HPOAnnotation> annotationMap = new LinkedHashMap<>();
        switch (format) {
            case TSVSingleFile:
                break;
            case TSVSeparateFile:
                break;
            case JSON:
                break;
            default:
                break;
        }
        return annotationMap;
    }


}

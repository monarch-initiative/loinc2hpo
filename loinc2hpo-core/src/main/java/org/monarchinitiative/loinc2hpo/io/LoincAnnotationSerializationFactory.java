package org.monarchinitiative.loinc2hpo.io;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;
import org.monarchinitiative.phenol.formats.hpo.HpoTerm;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoincAnnotationSerializationFactory {

    private static final Logger logger = LogManager.getLogger();

    public enum SerializationFormat {
        TSVSingleFile,
        TSVSeparateFile,
        JSON
    }

    public static void serializeToFile(Map<LoincId, UniversalLoinc2HPOAnnotation> annotationMap,
                                       SerializationFormat format, String path) throws Exception {
        switch (format) {
            case TSVSingleFile:
                LoincAnnotationSerializer serializer = new LoincAnnotationSerializerToTSVSingleFile();
                serializer.serialize(annotationMap, path);
                break;
            case TSVSeparateFile:
                break;
            case JSON:
                break;
            default:
                break;

        }
    }

    public static Map<LoincId, UniversalLoinc2HPOAnnotation> parseFromFile(String path, Map<TermId, HpoTerm> termmap, SerializationFormat format) throws Exception {
        Map<LoincId, UniversalLoinc2HPOAnnotation> annotationMap = new LinkedHashMap<>();
        switch (format) {
            case TSVSingleFile:
                logger.trace("entry TSVSingleFile serilizer:");
                LoincAnnotationSerializer serializer = new LoincAnnotationSerializerToTSVSingleFile(termmap);
                annotationMap = serializer.parse(path);
                logger.trace("annotationMap size: " + annotationMap.size());
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

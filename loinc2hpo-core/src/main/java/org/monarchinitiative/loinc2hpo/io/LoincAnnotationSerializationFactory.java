package org.monarchinitiative.loinc2hpo.io;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
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

    private static Map<LoincId, LoincEntry> loincEntryMap;
    private static Map<TermId, HpoTerm> hpoTermMap;

    /**
     * Use this method to set the LoincEntryMap that is used by TSVSeparatedFile deserializer
     */
    public static void setLoincEntryMap(Map<LoincId, LoincEntry> xloincEntryMap) {
        loincEntryMap = xloincEntryMap;
    }

    /**
     * Use this method to set the HPO TermMap that is used by all deserializers.
     * Can be replaced by calling parse() with the map as a parameter
     */
    public static void setHpoTermMap(Map<TermId, HpoTerm> termMap) {
        hpoTermMap = termMap;
    }


    public static void serializeToFile(Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap,
                                       SerializationFormat format, String path) throws IOException{

        if (hpoTermMap == null) {
            throw new NullPointerException("hpoTermMap not specified!");
        }
        LoincAnnotationSerializer serializer;
        switch (format) {
            case TSVSingleFile:
                serializer = new LoincAnnotationSerializerToTSVSingleFile(hpoTermMap);
                serializer.serialize(annotationMap, path);
                break;
            case TSVSeparateFile:
                if (loincEntryMap == null) {
                    throw new NullPointerException("loincEntryMap not specified!");
                }
                serializer = new LoincAnnotationSerializerToTSVSeparateFiles(hpoTermMap, loincEntryMap);
                serializer.serialize(annotationMap, path);
                break;
            case JSON:
                break;
            default:
                break;

        }
    }

    public static Map<LoincId, LOINC2HpoAnnotationImpl> parseFromFile(String path, Map<TermId, HpoTerm> termmap, SerializationFormat format) throws Exception {

        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap = new LinkedHashMap<>();
        LoincAnnotationSerializer serializer;
        switch (format) {
            case TSVSingleFile:
                logger.trace("entry TSVSingleFile serilizer:");
                serializer = new LoincAnnotationSerializerToTSVSingleFile(termmap);
                annotationMap = serializer.parse(path);
                logger.trace("annotationMap size: " + annotationMap.size());
                break;
            case TSVSeparateFile:
                serializer = new LoincAnnotationSerializerToTSVSeparateFiles(termmap, loincEntryMap);
                annotationMap = serializer.parse(path);
                break;
            case JSON:
                break;
            default:
                break;
        }
        return annotationMap;
    }


}

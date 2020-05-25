package org.monarchinitiative.loinc2hpocore.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpocore.Constants;
import org.monarchinitiative.loinc2hpocore.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpocore.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpocore.loinc.LoincScale;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoincAnnotationSerializerToTSVSeparateFiles implements LoincAnnotationSerializer {

    private static final Logger logger = LogManager.getLogger();

    private Map<LoincId, LoincEntry> loincEntryMap = null;

    private LoincAnnotationSerializerToTSVSeparateFiles() {

    }

    public LoincAnnotationSerializerToTSVSeparateFiles(Map<LoincId, LoincEntry> loincEntryMap) {

        this.loincEntryMap = loincEntryMap;

    }


    @Override
    /**
     * This method serializes annotation map to two files under filepath, basic_annotations.tsv and advanced_anotations.tsv. Retrieve those values from the constants class
     */
    public void serialize(Map<LoincId, LOINC2HpoAnnotationImpl> annotationmap, String filepath) throws IOException {

        if (loincEntryMap == null) {
            throw new NullPointerException("loincEntryMap is not provided yet");
        }
        String basicannotations = filepath + File.separator + Constants.TSVSeparateFilesBasic;
        String advancedAnnotations = filepath + File.separator + Constants.TSVSeparateFilesAdv;
        toTSVbasicAnnotations(basicannotations, annotationmap, loincEntryMap);
        toTSVadvancedAnnotations(advancedAnnotations, annotationmap);

    }

    @Override
    public Map<LoincId, LOINC2HpoAnnotationImpl> parse(String filepath) throws FileNotFoundException {

        if (loincEntryMap == null) {
            throw new NullPointerException("loincEntryMap is not provided yet");
        }

        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap;
        String basicannotations = filepath + File.separator + Constants.TSVSeparateFilesBasic;
        String advancedAnnotations = filepath + File.separator + Constants.TSVSeparateFilesAdv;
        if (new File(basicannotations).exists()) {
            annotationMap = fromTSVBasic(basicannotations);
        } else {
            annotationMap = new LinkedHashMap<>();
        }

        if (new File(advancedAnnotations).exists()) {
            fromTSVAdvanced(advancedAnnotations, annotationMap);
        }

        return annotationMap;
    }


    /**
     * Serialize the annotations in basic mode
     * @param path
     * @param annotationMap
     * @throws IOException
     */
    private void toTSVbasicAnnotations(String path, Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap, Map<LoincId, LoincEntry> loincEntryMap) throws IOException {

        logger.trace("enter toTSVbasicAnnotations() function");
        logger.trace("path: " + path);
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(LOINC2HpoAnnotationImpl.getHeader());

        for (LOINC2HpoAnnotationImpl annotation : annotationMap.values()) {
            writer.newLine();
            writer.write(basicAnnotations2String(annotation, loincEntryMap));
        }

        writer.close();
    }

    /**
     * Serialize the annotations in advanced mode
     * @param path
     * @param annotationMap
     * @throws IOException
     */
    private void toTSVadvancedAnnotations(String path, Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(LOINC2HpoAnnotationImpl.getHeader());

        for (LOINC2HpoAnnotationImpl annotation : annotationMap.values()) {
            String advancedstring = advancedAnnotations2String(annotation);
            if (advancedstring != null
                    && !advancedstring.isEmpty()) {
                writer.newLine();
                writer.write(advancedstring);
            }
        }

        writer.close();
    }


    public Map<LoincId, LOINC2HpoAnnotationImpl> fromTSVBasic(String path) throws FileNotFoundException {

        throw new UnsupportedOperationException();
    }


    public void fromTSVAdvanced(String path, Map<LoincId, LOINC2HpoAnnotationImpl> deserializedMap) throws FileNotFoundException {

        throw new UnsupportedOperationException();
    }

    private String basicAnnotations2String(LOINC2HpoAnnotationImpl annotation, Map<LoincId, LoincEntry> loincEntryMap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(annotation.getLoincId());
        stringBuilder.append("\t" + annotation.getLoincScale().toString());
        TermId low = null;
        TermId normal = null;
        TermId high = null;
        if (annotation.getLoincScale() == LoincScale.Qn) {
            low = annotation.getBelowNormalHpoTermId();
            normal = annotation.getNotAbnormalHpoTermName();
            high = annotation.getAboveNormalHpoTermName();
        } else if (annotation.getLoincScale() == LoincScale.Ord && loincEntryMap.get(annotation.getLoincId()).isPresentOrd()){
            normal = annotation.getNegativeHpoTermName();
            high = annotation.getPositiveHpoTermName();

        }
        stringBuilder.append("\t" + (low == null ? Constants.MISSINGVALUE : low.getValue()));
        stringBuilder.append("\t" + (normal == null ? Constants.MISSINGVALUE : normal.getValue()));
        stringBuilder.append("\t" + (high == null ? Constants.MISSINGVALUE : high.getValue()));
        stringBuilder.append("\t" + annotation.isNormalOrNegativeInversed());
        stringBuilder.append("\t" + (annotation.getNote() == null ? Constants.MISSINGVALUE : annotation.getNote()));
        stringBuilder.append("\t" + annotation.getFlag());
        stringBuilder.append("\t" + String.format("%.1f", annotation.getVersion()));
        stringBuilder.append("\t" + (annotation.getCreatedOn() == null ? Constants.MISSINGVALUE : annotation.getCreatedOn()));
        stringBuilder.append("\t" + (annotation.getCreatedBy() == null ? Constants.MISSINGVALUE : annotation.getCreatedBy()));
        stringBuilder.append("\t" + (annotation.getLastEditedOn() == null ? Constants.MISSINGVALUE: annotation.getLastEditedOn()));
        stringBuilder.append("\t" + (annotation.getLastEditedBy() == null ? Constants.MISSINGVALUE : annotation.getLastEditedBy()));
        //stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    private String advancedAnnotations2String(LOINC2HpoAnnotationImpl annotation) {

        throw new UnsupportedOperationException();

    }

}

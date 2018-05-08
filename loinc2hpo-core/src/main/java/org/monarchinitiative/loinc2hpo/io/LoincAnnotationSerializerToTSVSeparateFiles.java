package org.monarchinitiative.loinc2hpo.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.Constants;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.loinc.*;
import org.monarchinitiative.phenol.formats.hpo.HpoTerm;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoincAnnotationSerializerToTSVSeparateFiles implements LoincAnnotationSerializer {

    private static final Logger logger = LogManager.getLogger();

    private Map<TermId, HpoTerm> hpoTermMap = null;
    private Map<LoincId, LoincEntry> loincEntryMap = null;

    private LoincAnnotationSerializerToTSVSeparateFiles() {

    }

    public LoincAnnotationSerializerToTSVSeparateFiles(Map<TermId, HpoTerm> hpoTermMap, Map<LoincId, LoincEntry> loincEntryMap) {

        this.hpoTermMap = hpoTermMap;
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

        if (hpoTermMap == null) {
            throw new NullPointerException("hpoTermMap is not provided yet");
        }
        if (loincEntryMap == null) {
            throw new NullPointerException("loincEntryMap is not provided yet");
        }

        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap;
        String basicannotations = filepath + File.separator + Constants.TSVSeparateFilesBasic;
        String advancedAnnotations = filepath + File.separator + Constants.TSVSeparateFilesAdv;
        if (new File(basicannotations).exists()) {
            annotationMap = fromTSVBasic(basicannotations, this.hpoTermMap);
        } else {
            annotationMap = new LinkedHashMap<>();
        }

        if (new File(advancedAnnotations).exists()) {
            fromTSVAdvanced(advancedAnnotations, annotationMap, this.hpoTermMap);
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
        writer.write(LOINC2HpoAnnotationImpl.getHeaderBasic());

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
        writer.write(LOINC2HpoAnnotationImpl.getHeaderAdvanced());

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


    public Map<LoincId, LOINC2HpoAnnotationImpl> fromTSVBasic(String path, Map<TermId, HpoTerm> hpoTermMap) throws FileNotFoundException {

        Map<LoincId, LOINC2HpoAnnotationImpl> deserializedMap = new LinkedHashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        reader.lines().forEach(serialized -> {
            String[] elements = serialized.split("\\t");
            if (elements.length == 13 && !serialized.startsWith("loincId")) {
                try {
                    LoincId loincId = new LoincId(elements[0]);
                    LoincScale loincScale = LoincScale.string2enum(elements[1]);
                    TermId low = convertToTermID(elements[2]);
                    TermId intermediate = convertToTermID(elements[3]);
                    TermId high = convertToTermID(elements[4]);
                    logger.trace(String.format("low: %s; normal: %s; high: %s", low, intermediate, high));
                    boolean inverse = Boolean.parseBoolean(elements[5]);
                    String note = elements[6].equals(Constants.MISSINGVALUE) ? null : elements[6];
                    boolean flag = Boolean.parseBoolean(elements[7]);
                    double version = Double.parseDouble(elements[8]);
                    LocalDateTime createdOn = elements[9].equals(Constants.MISSINGVALUE) ? null : LocalDateTime.parse(elements[9]);
                    String createdBy = elements[10].equals(Constants.MISSINGVALUE)? null : elements[10];
                    LocalDateTime lastEditedOn = elements[11].equals(Constants.MISSINGVALUE)? null : LocalDateTime.parse(elements[11]);
                    String lastEditedBy = elements[12].equals(Constants.MISSINGVALUE)? null : elements[12];

                    if (!deserializedMap.containsKey(loincId)) {
                        LOINC2HpoAnnotationImpl.Builder builder = new LOINC2HpoAnnotationImpl.Builder();
                        builder.setLoincId(loincId)
                                .setLoincScale(loincScale)
                                .setCreatedOn(createdOn)
                                .setCreatedBy(createdBy)
                                .setLastEditedOn(lastEditedOn)
                                .setLastEditedBy(lastEditedBy)
                                .setVersion(version)
                                .setNote(note)
                                .setFlag(flag);
                        if (loincScale == LoincScale.Qn) {
                            builder.setLowValueHpoTerm(hpoTermMap.get(low))
                                    .setIntermediateValueHpoTerm(hpoTermMap.get(intermediate))
                                    .setHighValueHpoTerm(hpoTermMap.get(high))
                                    .setIntermediateNegated(inverse);
                        } else if (loincScale == LoincScale.Ord && loincEntryMap.get(loincId).isPresentOrd()) {
                            builder.setNegValueHpoTerm(hpoTermMap.get(intermediate), inverse)
                                    .setPosValueHpoTerm(hpoTermMap.get(high));
                        } else {
                            builder.setLowValueHpoTerm(hpoTermMap.get(low))
                                    .setIntermediateValueHpoTerm(hpoTermMap.get(intermediate))
                                    .setHighValueHpoTerm(hpoTermMap.get(high))
                                    .setIntermediateNegated(inverse)
                                    .setNegValueHpoTerm(hpoTermMap.get(intermediate), inverse)
                                    .setPosValueHpoTerm(hpoTermMap.get(high));
                        }
                        deserializedMap.put(loincId, builder.build());
                        logger.trace(deserializedMap.get(loincId));
                    }
                } catch (MalformedLoincCodeException e) {
                    logger.error("Malformed loinc code line: " + serialized);
                }
            } else {
                if (elements.length != 13) {
                    logger.error(String.format("line does not have 13 elements, but has %d elements. Line: %s",
                            elements.length,  serialized));
                } else {
                    logger.info("line is header: " + serialized);
                }

            }
        });

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deserializedMap;
    }


    public void fromTSVAdvanced(String path, Map<LoincId, LOINC2HpoAnnotationImpl> deserializedMap, Map<TermId, HpoTerm> hpoTermMap) throws FileNotFoundException {

        BufferedReader reader = new BufferedReader(new FileReader(path));
        reader.lines().forEach(serialized -> {
            String[] elements = serialized.split("\\t");
            if (elements.length == 13 && !serialized.startsWith("loincId")) {
                try {
                    LoincId loincId = new LoincId(elements[0]);
                    String system = elements[2];
                    String codeString = elements[3];
                    TermId termId = convertToTermID(elements[4]);
                    boolean inverse = Boolean.parseBoolean(elements[5]);
                    LOINC2HpoAnnotationImpl annotation = deserializedMap.get(loincId);
                    Code code = Code.getNewCode().setSystem(system).setCode(codeString);
                    annotation.addAdvancedAnnotation(code, new HpoTerm4TestOutcome(hpoTermMap.get(termId), inverse));
                } catch (MalformedLoincCodeException e) {
                    logger.error("Malformed loinc code line: " + serialized);
                }
            } else {
                if (elements.length != 13) {
                    logger.error(String.format("line does not have 13 elements, but has %d elements. Line: %s",
                            elements.length,  serialized));
                } else {
                    logger.info("line is header: " + serialized);
                }

            }
        });

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        stringBuilder.append("\t" + (low == null ? Constants.MISSINGVALUE : low.getIdWithPrefix()));
        stringBuilder.append("\t" + (normal == null ? Constants.MISSINGVALUE : normal.getIdWithPrefix()));
        stringBuilder.append("\t" + (high == null ? Constants.MISSINGVALUE : high.getIdWithPrefix()));
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

        Map<String, Code> internalCode = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);

        Map<Code, HpoTerm4TestOutcome> advancedAnnotationTerms;
        advancedAnnotationTerms = new LinkedHashMap<>(annotation.getCandidateHpoTerms());
        if (annotation.getLoincScale() == LoincScale.Qn) {
            advancedAnnotationTerms.remove(internalCode.get("N"));
            advancedAnnotationTerms.remove(internalCode.get("A"));
            advancedAnnotationTerms.remove(internalCode.get("H"));
            advancedAnnotationTerms.remove(internalCode.get("L"));
        } else if (annotation.getLoincScale() == LoincScale.Ord && loincEntryMap.get(annotation.getLoincId()).isPresentOrd()) {
            advancedAnnotationTerms.remove(internalCode.get("NEG"));
            advancedAnnotationTerms.remove(internalCode.get("POS"));
        }
        if (advancedAnnotationTerms.containsKey(internalCode.get("N")) && advancedAnnotationTerms.containsKey(internalCode.get("NEG"))) {
            advancedAnnotationTerms.remove(internalCode.get("N")); //it would be already saved in basic annotations
        }
        if (advancedAnnotationTerms.containsKey(internalCode.get("H")) && advancedAnnotationTerms.containsKey(internalCode.get("POS"))) {
            advancedAnnotationTerms.remove(internalCode.get("H")); //it would be already saved in basic annotations
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (advancedAnnotationTerms.isEmpty()) {
            return null;
        }
        advancedAnnotationTerms.forEach((code, hpoTermId4LoincTest) -> {
            stringBuilder.append(annotation.getLoincId());
            stringBuilder.append("\t" + annotation.getLoincScale().toString());
            stringBuilder.append("\t" + code.getSystem());
            stringBuilder.append("\t" + code.getCode());
            stringBuilder.append("\t" + hpoTermId4LoincTest.getId().getIdWithPrefix());
            stringBuilder.append("\t" + hpoTermId4LoincTest.isNegated());
            stringBuilder.append("\t" + (annotation.getNote() == null ? Constants.MISSINGVALUE : annotation.getNote()));
            stringBuilder.append("\t" + annotation.getFlag());
            stringBuilder.append("\t" + String.format("%.1f", annotation.getVersion()));
            stringBuilder.append("\t" + (annotation.getCreatedOn() == null ? Constants.MISSINGVALUE : annotation.getCreatedOn()));
            stringBuilder.append("\t" + (annotation.getCreatedBy() == null ? Constants.MISSINGVALUE : annotation.getCreatedBy()));
            stringBuilder.append("\t" + (annotation.getLastEditedOn() == null ? Constants.MISSINGVALUE : annotation.getLastEditedOn()));
            stringBuilder.append("\t" + (annotation.getLastEditedBy() == null ? Constants.MISSINGVALUE : annotation.getLastEditedBy()));
            stringBuilder.append("\n");
        });

        return stringBuilder.toString().trim();

    }

}

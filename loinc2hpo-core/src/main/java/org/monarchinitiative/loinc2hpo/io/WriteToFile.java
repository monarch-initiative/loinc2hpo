package org.monarchinitiative.loinc2hpo.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermPrefix;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.github.phenomics.ontolib.ontology.data.TermPrefix;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincScale;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;


import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class WriteToFile {

    private static final Logger logger = LogManager.getLogger();
    private final static String MISSINGVALUE = "NA";

    public static void writeToFile(String content, String pathToFile) {

        try (FileWriter fileWriter = new FileWriter(pathToFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(content);
        } catch (IOException e) {
            logger.error("Error occured when trying to save to a new file");
        }

    }

    public static void appendToFile(String content, String pathToFile) {

        try (FileWriter fileWriter = new FileWriter(pathToFile, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)){
            bufferedWriter.write(content);

        } catch (IOException e) {
            logger.error("Error occured when trying to append to a file");
        }

    }

    public static <K extends Serializable, V extends Serializable> void serialize(Map<K, V> map, String pathToFile) {

        try (FileOutputStream fileOutputStream = new FileOutputStream(pathToFile);
             ObjectOutputStream out = new ObjectOutputStream(fileOutputStream)) {
            out.writeObject(map);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <K extends Serializable, V extends Serializable> Map<K, V> deserialize(String pathToFile) {
        try (FileInputStream fileInputStream = new FileInputStream(pathToFile);
             ObjectInputStream in = new ObjectInputStream(fileInputStream)) {
            Map<K, V> map = (Map<K, V>) in.readObject();
            return map;
        } catch (FileNotFoundException e) {
            logger.error("File is not found for deserialization");

        } catch (IOException e) {
            logger.error("IO error when trying to read deserialization file: " + pathToFile);

        } catch (ClassNotFoundException e) {
            logger.error("Class mismatch: deserizlized objects does not match expectation");

        }
        return null;
    }

    public static void toJson(Map<LoincId, UniversalLoinc2HPOAnnotation> annotationMap){

        ObjectMapper mapper = new ObjectMapper();
        annotationMap.entrySet().forEach(p -> {
            try {
                System.out.println(mapper.writeValueAsString(p));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });


    }

    public static Map<LoincId, UniversalLoinc2HPOAnnotation> fromJson(String path) {

        return null;
    }


    public static void toTSV(String path, Map<LoincId, UniversalLoinc2HPOAnnotation> annotationMap) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(UniversalLoinc2HPOAnnotation.getHeaderAdvanced());

        for (UniversalLoinc2HPOAnnotation annotation : annotationMap.values()) {
            writer.newLine();
            writer.write(annotation.toString());
        }

        writer.close();
    }

    /**
     * Serialize the annotations in basic mode
     * @param path
     * @param annotationMap
     * @throws IOException
     */
    public static void toTSVbasicAnnotations(String path, Map<LoincId, UniversalLoinc2HPOAnnotation> annotationMap) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(UniversalLoinc2HPOAnnotation.getHeaderBasic());

        for (UniversalLoinc2HPOAnnotation annotation : annotationMap.values()) {
            writer.newLine();
            writer.write(annotation.getBasicAnnotationsString());
        }

        writer.close();
    }

    /**
     * Serialize the annotations in advanced mode
     * @param path
     * @param annotationMap
     * @throws IOException
     */
    public static void toTSVadvancedAnnotations(String path, Map<LoincId, UniversalLoinc2HPOAnnotation> annotationMap) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(UniversalLoinc2HPOAnnotation.getHeaderAdvanced());

        for (UniversalLoinc2HPOAnnotation annotation : annotationMap.values()) {
            if (annotation.getAdvancedAnnotationsString() != null
                    && !annotation.getAdvancedAnnotationsString().isEmpty()) {
                writer.newLine();
                writer.write(annotation.getAdvancedAnnotationsString());
            }
        }

        writer.close();
    }

    public static void appendtoTSV(String path, Map<LoincId, UniversalLoinc2HPOAnnotation> annotationMap) throws IOException {

        StringBuilder builder = new StringBuilder();
        for (UniversalLoinc2HPOAnnotation annotation : annotationMap.values()) {
            builder.append("\n");
            builder.append(annotation.toString());
        }
        appendToFile(builder.toString(), path);
    }


    /**
     * A method to deserialize annotation map from a TSV file.
     * @param path filepath to the TSV
     * @param hpoTermMap a HPO map from TermId to HpoTerm. Note: the key is TermId, instead of TermName
     * @return an annotation map
     * @throws FileNotFoundException
     */

    public static Map<LoincId, UniversalLoinc2HPOAnnotation> fromTSV(String path, Map<TermId, HpoTerm> hpoTermMap) throws FileNotFoundException {

        Map<LoincId, UniversalLoinc2HPOAnnotation> deserializedMap = new LinkedHashMap<>();
        Map<LoincId, UniversalLoinc2HPOAnnotation.Builder> builderMap = new HashMap<>();
        Map<String, Code> internalCode = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
        BufferedReader reader = new BufferedReader(new FileReader(path));
        reader.lines().forEach(serialized -> {
            String[] elements = serialized.split("\\t");
            if (elements.length == 13 && !serialized.startsWith("loincId")) {
                try {
                    LoincId loincId = new LoincId(elements[0]);
                    LoincScale loincScale = LoincScale.string2enum(elements[1]);
                    String codeSystem = elements[2];
                    String codeId = elements[3];
                    TermPrefix prefix = new ImmutableTermPrefix(elements[4].substring(0, 2));
                    String id = elements[4].substring(3);
                    HpoTerm hpoTerm = hpoTermMap.get(new ImmutableTermId(prefix, id));
                    boolean inverse = Boolean.parseBoolean(elements[5]);
                    String note = elements[6].equals(MISSINGVALUE) ? null : elements[6];
                    boolean flag = Boolean.parseBoolean(elements[7]);
                    double version = Double.parseDouble(elements[8]);
                    LocalDateTime createdOn = elements[9].equals(MISSINGVALUE) ? null : LocalDateTime.parse(elements[9]);
                    String createdBy = elements[10].equals(MISSINGVALUE)? null : elements[10];
                    LocalDateTime lastEditedOn = elements[11].equals(MISSINGVALUE)? null : LocalDateTime.parse(elements[11]);
                    String lastEditedBy = elements[12].equals(MISSINGVALUE)? null : elements[12];

                    if (!builderMap.containsKey(loincId)) {
                        UniversalLoinc2HPOAnnotation.Builder builder = new UniversalLoinc2HPOAnnotation.Builder()
                                .setLoincId(loincId)
                                .setLoincScale(loincScale)
                                .setNote(note).setFlag(flag)
                                .setVersion(version)
                                .setCreatedOn(createdOn).setCreatedBy(createdBy)
                                .setLastEditedOn(lastEditedOn).setLastEditedBy(lastEditedBy);
                        builderMap.put(loincId, builder);
                    }
                    Code code = Code.getNewCode().setSystem(codeSystem).setCode(codeId);
                    HpoTermId4LoincTest hpoTermId4LoincTest = new HpoTermId4LoincTest(hpoTerm, inverse);
                    if (code.equals(internalCode.get("L"))) {
                        builderMap.get(loincId).setLowValueHpoTerm(hpoTermId4LoincTest.getHpoTerm());
                    }
                    if (code.equals(internalCode.get("N"))) {
                        builderMap.get(loincId).setIntermediateValueHpoTerm(hpoTermId4LoincTest.getHpoTerm());
                        builderMap.get(loincId).setIntermediateNegated(hpoTermId4LoincTest.isNegated());
                    }
                    if (code.equals(internalCode.get("H"))) {
                        builderMap.get(loincId).setHighValueHpoTerm(hpoTermId4LoincTest.getHpoTerm());
                    }
                    if (code.equals(internalCode.get("A"))
                            || code.equals(internalCode.get("POS"))
                            || code.equals(internalCode.get("NEG"))) {
                        //currently, we neglect those codes
                        //it will be wrong to do so if the user has manually changed what map to them
                        logger.info("!!!!!!!!!!!annotation neglected. MAY BE WRONG!!!!!!!!!!!!!!!");
                    }  else {
                        builderMap.get(loincId).addAdvancedAnnotation(code, hpoTermId4LoincTest);
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

        builderMap.entrySet().forEach(b -> deserializedMap.put(b.getKey(), b.getValue().build()));
        return deserializedMap;
    }


    public static Map<LoincId, UniversalLoinc2HPOAnnotation> fromTSVBasic(String path, Map<TermId, HpoTerm> hpoTermMap) throws FileNotFoundException {

        Map<LoincId, UniversalLoinc2HPOAnnotation> deserializedMap = new LinkedHashMap<>();
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
                    String note = elements[6].equals(MISSINGVALUE) ? null : elements[6];
                    boolean flag = Boolean.parseBoolean(elements[7]);
                    double version = Double.parseDouble(elements[8]);
                    LocalDateTime createdOn = elements[9].equals(MISSINGVALUE) ? null : LocalDateTime.parse(elements[9]);
                    String createdBy = elements[10].equals(MISSINGVALUE)? null : elements[10];
                    LocalDateTime lastEditedOn = elements[11].equals(MISSINGVALUE)? null : LocalDateTime.parse(elements[11]);
                    String lastEditedBy = elements[12].equals(MISSINGVALUE)? null : elements[12];

                    if (!deserializedMap.containsKey(loincId)) {
                        UniversalLoinc2HPOAnnotation.Builder builder = new UniversalLoinc2HPOAnnotation.Builder();
                        builder.setLoincId(loincId)
                                .setLoincScale(loincScale)
                                .setLowValueHpoTerm(hpoTermMap.get(low))
                                .setIntermediateValueHpoTerm(hpoTermMap.get(intermediate))
                                .setHighValueHpoTerm(hpoTermMap.get(high))
                                .setIntermediateNegated(inverse)
                                .setCreatedOn(createdOn)
                                .setCreatedBy(createdBy)
                                .setLastEditedOn(lastEditedOn)
                                .setLastEditedBy(lastEditedBy)
                                .setVersion(version)
                                .setNote(note)
                                .setFlag(flag);

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


    public static void fromTSVAdvanced(String path, Map<LoincId, UniversalLoinc2HPOAnnotation> deserializedMap, Map<TermId, HpoTerm> hpoTermMap) throws FileNotFoundException {

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
                    UniversalLoinc2HPOAnnotation annotation = deserializedMap.get(loincId);
                    Code code = Code.getNewCode().setSystem(system).setCode(codeString);
                    annotation.addAdvancedAnnotation(code, new HpoTermId4LoincTest(hpoTermMap.get(termId), inverse));
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


    public static TermId convertToTermID(String record) {
        TermPrefix prefix = new ImmutableTermPrefix("HP");
        if (!record.startsWith(prefix.getValue()) || record.length() <= 3) {
            logger.error("Non HPO termId is detected from TSV: " + record);
            return null;
        }
        String id = record.substring(3);
        return new ImmutableTermId(prefix, id);
    }
}

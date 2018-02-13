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
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.loinc.HpoTermId4LoincTest;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincScale;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class WriteToFile {

    private static final Logger logger = LogManager.getLogger();

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
        writer.write(UniversalLoinc2HPOAnnotation.getHeader());

        for (UniversalLoinc2HPOAnnotation annotation : annotationMap.values()) {
            writer.newLine();
            writer.write(annotation.toString());
        }

        writer.close();
    }


    /**
     * A method to deserialize annotation map from a TSV file.
     * @param path filepath to the TSV
     * @param hpoTermMap a HPO map from TermId to HpoTerm. Note: the key is TermId, instead of TermName
     * @return an annotation map
     * @throws FileNotFoundException
     */
    public static Map<LoincId, UniversalLoinc2HPOAnnotation> fromTSV(String path, Map<TermId, HpoTerm> hpoTermMap) throws FileNotFoundException {

        Map<LoincId, UniversalLoinc2HPOAnnotation> deserializedMap = new HashMap<>();
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
                    String note = elements[6].equals("null") ? null : elements[6];
                    boolean flag = Boolean.parseBoolean(elements[7]);
                    double version = Double.parseDouble(elements[8]);
                    LocalDate createdOn = elements[9].equals("null") ? null : LocalDate.parse(elements[9]);
                    String createdBy = elements[10].equals("null")? null : elements[10];
                    LocalDate lastEditedOn = elements[11].equals("null")? null : LocalDate.parse(elements[11]);
                    String lastEditedBy = elements[12].equals("null")? null : elements[12];

                    if (!deserializedMap.containsKey(loincId)) {
                        UniversalLoinc2HPOAnnotation annotation = new UniversalLoinc2HPOAnnotation(loincId, loincScale)
                                .setNote(note).setFlag(flag)
                                .setVersion(version)
                                .setCreatedOn(createdOn).setCreatedBy(createdBy)
                                .setLastEditedOn(lastEditedOn).setLastEditedBy(lastEditedBy);
                        deserializedMap.put(loincId, annotation);
                    }
                    Code code = Code.getNewCode().setSystem(codeSystem).setCode(codeId);
                    HpoTermId4LoincTest hpoTermId4LoincTest = new HpoTermId4LoincTest(hpoTerm.getId(), inverse);
                    deserializedMap.get(loincId).addAnnotation(code, hpoTermId4LoincTest);
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
}

package org.monarchinitiative.loinc2hpo.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
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


}

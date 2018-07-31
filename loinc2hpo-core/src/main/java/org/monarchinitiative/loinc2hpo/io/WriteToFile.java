package org.monarchinitiative.loinc2hpo.io;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;


import java.io.*;
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


    public static TermId convertToTermID(String record) {
        TermPrefix prefix = new TermPrefix("HP");
        if (!record.startsWith(prefix.getValue()) || record.length() <= 3) {
            logger.error("Non HPO termId is detected from TSV: " + record);
            return null;
        }
        String id = record.substring(3);
        return new TermId(prefix, id);
    }
}

package org.monarchinitiative.loinc2hpo.io;

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

    public static void serializeObject(Map<LoincId, UniversalLoinc2HPOAnnotation> annotationMap, String pathToFile) {

        try (FileOutputStream fileOutputStream = new FileOutputStream(pathToFile);
             ObjectOutputStream out = new ObjectOutputStream(fileOutputStream)) {
            out.writeObject(annotationMap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<LoincId, UniversalLoinc2HPOAnnotation> deserializeObject(String pathToFile) {
        try (FileInputStream fileInputStream = new FileInputStream(pathToFile);
             ObjectInputStream in = new ObjectInputStream(fileInputStream)) {
            Map<LoincId, UniversalLoinc2HPOAnnotation> map = (Map<LoincId, UniversalLoinc2HPOAnnotation>) in.readObject();
            return map;
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } catch (ClassNotFoundException e) {

        }
        return null;
    }

}

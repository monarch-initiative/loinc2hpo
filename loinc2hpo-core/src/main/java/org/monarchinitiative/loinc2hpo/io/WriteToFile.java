package org.monarchinitiative.loinc2hpo.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

}

package org.monarchinitiative.loinc2hpo.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class Model {
    private static final Logger logger = LogManager.getLogger();

    private String pathToLoincCoreTableFile=null;

    private String biocuratorname=null;

    private String pathToSettingsFile=null;


    private String pathToAnnotationFile=null;

    public void setPathToLoincCoreTableFile(String pathToLoincCoreTableFile) {
        this.pathToLoincCoreTableFile = pathToLoincCoreTableFile;
    }

    public void setPathToSettingsFile(String p) { this.pathToSettingsFile=p;}

    public void setPathToAnnotationFile(String p) {pathToAnnotationFile=p;}


    public String getPathToLoincCoreTableFile() {
        return pathToLoincCoreTableFile;
    }


    public Model() {
        init();
    }



    private void init() {

    }


    public void writeSettings() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(pathToSettingsFile));
            if (biocuratorname!=null) {
                bw.write(String.format("biocuratorname:%s",biocuratorname));
            }
            if (pathToLoincCoreTableFile!=null) {
                bw.write(String.format("loincTablePath:%s",pathToLoincCoreTableFile));
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Could not write settings at " + pathToSettingsFile);
        }
    }

    public void setSettings(final String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                String A[]=line.split(":");
                if (A[0].equals("biocuratorname")) this.biocuratorname=A[1].trim();
                else if (A[0].equals("loincTablePath")) this.pathToLoincCoreTableFile=A[1].trim();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Could not open settings at " + path);
        }
    }

}

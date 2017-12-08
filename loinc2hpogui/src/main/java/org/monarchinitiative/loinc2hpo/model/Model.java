package org.monarchinitiative.loinc2hpo.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Model {
    private static final Logger logger = LogManager.getLogger();

    public String getPathToLoincCoreTableFile() {
        return pathToLoincCoreTableFile;
    }

    public void setPathToLoincCoreTableFile(String pathToLoincCoreTableFile) {
        this.pathToLoincCoreTableFile = pathToLoincCoreTableFile;
    }

    private String pathToLoincCoreTableFile=null;


    public Model() {

    }




}

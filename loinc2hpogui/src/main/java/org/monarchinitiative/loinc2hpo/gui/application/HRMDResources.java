package org.monarchinitiative.loinc2hpo.gui.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a POJO holding paths to HRMD resources. Created by Daniel Danis on 7/16/17.
 */
public final class HRMDResources {
    /* NOTE: If a new resource is added here, don't forget to add it also to this.getResourceMap() method. */

    private StringProperty dataDir = new SimpleStringProperty(this, "dataDir");

    private StringProperty refGenomeDir = new SimpleStringProperty(this, "refGenomeDir");

    private StringProperty hpOBOPath = new SimpleStringProperty(this, "hpOBOPath");

    private StringProperty entrezGenePath = new SimpleStringProperty(this, "entrezGenePath");

    private StringProperty diseaseCaseDir = new SimpleStringProperty(this, "diseaseCaseDir");

    private StringProperty biocuratorId = new SimpleStringProperty(this, "biocuratorId");

    @JsonCreator
    public HRMDResources(
            @JsonProperty("dataDir") String dataDir,
            @JsonProperty("refGenome") String refGenomeDir,
            @JsonProperty("hpOBOPath") String hpOBOPath,
            @JsonProperty("entrezGeneFilePath") String entrezGenePath,
            @JsonProperty("diseaseCaseDir") String diseaseCaseDir,
            @JsonProperty("biocuratorId") String biocuratorId) {
        this.dataDir.set(dataDir);
        this.refGenomeDir.set(refGenomeDir);
        this.hpOBOPath.set(hpOBOPath);
        this.entrezGenePath.set(entrezGenePath);
        this.diseaseCaseDir.set(diseaseCaseDir);
        this.biocuratorId.set(biocuratorId);
    }

    @JsonGetter("dataDir")
    public String getDataDir() {
        return dataDir.get();
    }

    public void setDataDir(String dataDir) {
        this.dataDir.set(dataDir);
    }

    public StringProperty dataDirProperty() {
        return dataDir;
    }


    @JsonGetter("refGenome")
    public String getRefGenomeDir() {
        return refGenomeDir.get();
    }

    public void setRefGenomeDir(String refGenomeDir) {
        this.refGenomeDir.set(refGenomeDir);
    }

    public StringProperty refGenomeDirProperty() {
        return refGenomeDir;
    }

    @JsonGetter("hpOBOPath")
    public String getHpOBOPath() {
        return hpOBOPath.get();
    }

    public void setHpOBOPath(String hpOBOPath) {
        this.hpOBOPath.set(hpOBOPath);
    }

    public StringProperty hpOBOPathProperty() {
        return hpOBOPath;
    }

    @JsonGetter("entrezGeneFilePath")
    public String getEntrezGenePath() {
        return entrezGenePath.get();
    }

    public void setEntrezGenePath(String entrezGenePath) {
        this.entrezGenePath.set(entrezGenePath);
    }

    public StringProperty entrezGenePathProperty() {
        return entrezGenePath;
    }

    @JsonGetter("diseaseCaseDir")
    public String getDiseaseCaseDir() {
        return diseaseCaseDir.get();
    }

    public void setDiseaseCaseDir(String diseaseCaseDir) {
        this.diseaseCaseDir.set(diseaseCaseDir);
    }

    public StringProperty diseaseCaseDirProperty() {
        return diseaseCaseDir;
    }

    @JsonGetter("biocuratorId")
    public String getBiocuratorId() {
        return biocuratorId.get();
    }

    public void setBiocuratorId(String biocuratorId) {
        this.biocuratorId.set(biocuratorId);
    }

    public StringProperty biocuratorIdProperty() {
        return biocuratorId;
    }

    @Override
    public String toString() {
        return "HRMDResources{" +
                "refGenomeDir='" + refGenomeDir.get() + '\'' +
                ", hpOBOPath='" + hpOBOPath.get() + '\'' +
                ", entrezGenePath='" + entrezGenePath.get() + '\'' +
                ", diseaseCaseDir='" + diseaseCaseDir.get() + "\'" +
                ", dataDir='" + dataDir.get() + "\'" +
                ", biocuratorId='" + biocuratorId.get() + "\'" +
                '}';
    }

    /**
     * Get Map containing human-readable resource description as keys and resource values as values. The Map content is
     * presented to user by {@link org.monarchinitiative.hrmd_gui.controller.ShowResourcesController}.
     *
     * @return {@link Map} of Strings as keys and Objects as entries.
     */
    public Map<String, Object> getResourceMap() {
        Map<String, Object> resourceMap = new HashMap<>();
        resourceMap.put("Data directory", getDataDir());
        resourceMap.put("Reference genome directory", getRefGenomeDir());
        resourceMap.put("Path to HP.obo file", getHpOBOPath());
        resourceMap.put("Path to Entrez file", getEntrezGenePath());
        resourceMap.put("Path to directory with model files", getDiseaseCaseDir());
        resourceMap.put("Biocurator ID", getBiocuratorId());
        return resourceMap;
    }
}

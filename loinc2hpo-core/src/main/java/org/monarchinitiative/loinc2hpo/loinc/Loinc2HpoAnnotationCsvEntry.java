package org.monarchinitiative.loinc2hpo.loinc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Loinc2HpoAnnotationCsvEntry {

    private String loincId;
    private String loincScale;
    private String system;
    private String code;
    private String hpoTermId;
    private String isNegated;
    private String createdOn;
    private String createdBy;
    private String lastEditedOn;
    private String lastEditedBy;
    private String version;
    private String isFinalized;
    private String comment;


    public Loinc2HpoAnnotationCsvEntry(String loincId, String loincScale, String system, String code, String hpoTermId, String isNegated, String createdOn, String createdBy, String lastEditedOn, String lastEditedBy, String version, String isFinalized, String comment) {
        this.loincId = loincId;
        this.loincScale = loincScale;
        this.system = system;
        this.code = code;
        this.hpoTermId = hpoTermId;
        this.isNegated = isNegated;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.lastEditedOn = lastEditedOn;
        this.lastEditedBy = lastEditedBy;
        this.version = version;
        this.isFinalized = isFinalized;
        this.comment = comment;
    }


    public static List<Loinc2HpoAnnotationCsvEntry> importAnnotations(String path) throws IOException {
        List<Loinc2HpoAnnotationCsvEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))){
            //read header
            String line = reader.readLine();
            if (!line.equals("loincId\tloincScale\tsystem\tcode\thpoTermId\tisNegated\tcreatedOn\tcreatedBy\tlastEditedOn\tlastEditedBy\tversion\tisFinalized\tcomment")){
                throw new RuntimeException("header does not match expected!");
            }

            while ((line = reader.readLine()) != null){
                String[] elements = line.split("\t");
                if (elements.length != 13){
                    throw new RuntimeException("Line does not have expected length: " + line);
                }
                String loincId = elements[0];
                String loincScale = elements[1];
                String system = elements[2];
                String code = elements[3];
                String hpoTermId = elements[4];
                String isNegated = elements[5];
                String createdOn = elements[6];
                String createdBy = elements[7];
                String lastEditedOn = elements[8];
                String lastEditedBy = elements[9];
                String version = elements[10];
                String isFinalized = elements[11];
                String comment = elements[12];
                Loinc2HpoAnnotationCsvEntry newEntry = new Loinc2HpoAnnotationCsvEntry.Builder()
                        .withLoincId(loincId)
                        .withLoincScale(loincScale)
                        .withSystem(system)
                        .withCode(code)
                        .withHpoTermId(hpoTermId)
                        .withIsNegated(isNegated)
                        .withCreatedOn(createdOn)
                        .withCreatedBy(createdBy)
                        .withLastEditedOn(lastEditedOn)
                        .withLastEditedBy(lastEditedBy)
                        .withVersion(version)
                        .withIsFinalized(isFinalized)
                        .withComment(comment)
                        .build();
                entries.add(newEntry);
            }
        }
        return entries;
    }


    public String getLoincId() {
        return loincId;
    }

    public void setLoincId(String loincId) {
        this.loincId = loincId;
    }

    public String getLoincScale() {
        return loincScale;
    }

    public void setLoincScale(String loincScale) {
        this.loincScale = loincScale;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHpoTermId() {
        return hpoTermId;
    }

    public void setHpoTermId(String hpoTermId) {
        this.hpoTermId = hpoTermId;
    }

    public String getIsNegated() {
        return isNegated;
    }

    public void setIsNegated(String isNegated) {
        this.isNegated = isNegated;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastEditedOn() {
        return lastEditedOn;
    }

    public void setLastEditedOn(String lastEditedOn) {
        this.lastEditedOn = lastEditedOn;
    }

    public String getLastEditedBy() {
        return lastEditedBy;
    }

    public void setLastEditedBy(String lastEditedBy) {
        this.lastEditedBy = lastEditedBy;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIsFinalized() {
        return isFinalized;
    }

    public void setIsFinalized(String isFinalized) {
        this.isFinalized = isFinalized;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public static final class Builder {
        private String loincId;
        private String loincScale;
        private String system;
        private String code;
        private String hpoTermId;
        private String isNegated;
        private String createdOn;
        private String createdBy;
        private String lastEditedOn;
        private String lastEditedBy;
        private String version;
        private String isFinalized;
        private String comment;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withLoincId(String loincId) {
            this.loincId = loincId;
            return this;
        }

        public Builder withLoincScale(String loincScale) {
            this.loincScale = loincScale;
            return this;
        }

        public Builder withSystem(String system) {
            this.system = system;
            return this;
        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withHpoTermId(String hpoTermId) {
            this.hpoTermId = hpoTermId;
            return this;
        }

        public Builder withIsNegated(String isNegated) {
            this.isNegated = isNegated;
            return this;
        }

        public Builder withCreatedOn(String createdOn) {
            this.createdOn = createdOn;
            return this;
        }

        public Builder withCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder withLastEditedOn(String lastEditedOn) {
            this.lastEditedOn = lastEditedOn;
            return this;
        }

        public Builder withLastEditedBy(String lastEditedBy) {
            this.lastEditedBy = lastEditedBy;
            return this;
        }

        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder withIsFinalized(String isFinalized) {
            this.isFinalized = isFinalized;
            return this;
        }

        public Builder withComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Loinc2HpoAnnotationCsvEntry build() {
            return new Loinc2HpoAnnotationCsvEntry(loincId, loincScale, system, code, hpoTermId, isNegated, createdOn, createdBy, lastEditedOn, lastEditedBy, version, isFinalized, comment);
        }
    }
}

package org.monarchinitiative.loinc2hpocore.loinc;

import org.apache.commons.lang3.StringUtils;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.exception.MalformedLoincCodeException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


/**
 * This is simplied Loinc2HpoAnnotationModel
 */
public class Loinc2HpoAnnotationModel {

    private LoincId loincId;
    private LoincScale loincScale;
    private Map<Code, HpoTerm4TestOutcome> annotations;
    private LocalDateTime createdOn;
    private String createdBy;
    private LocalDateTime lastEditedOn;
    private String lastEditedBy;
    private String version;
    private boolean isFinalized;
    private String comment;

    public static final String csv_header(String delim){

        return StringUtils.join(
                Arrays.asList("loincId",
                "loincScale",
                "system",
                "code",
                "hpoTermId",
                "isNegated",
                "createdOn",
                "createdBy",
                "lastEditedOn",
                "lastEditedBy",
                "version",
                "isFinalized",
                "comment"), delim);

    }

    public static List<Loinc2HpoAnnotationCsvEntry> to_csv_entries(Loinc2HpoAnnotationModel dataModel){

        //convert objects to string
        String loincId = dataModel.getLoincId().toString();
        String loincScale = dataModel.getLoincScale().toString();
        String createdOn = dataModel.getCreatedOn() == null ? "NA" : dataModel.getCreatedOn().toString();
        String createdBy = dataModel.getCreatedBy();
        String lastEditedOn = dataModel.getLastEditedOn() == null ? "NA" : dataModel.getLastEditedOn().toString();
        String lastEditedBy = dataModel.getLastEditedBy();
        String version = dataModel.getVersion();
        String isFinalized = dataModel.isFinalized()? "true" : "false";
        String comment = dataModel.getComment();

        List<Loinc2HpoAnnotationCsvEntry> entries = new ArrayList<>();
        for (Map.Entry<Code, HpoTerm4TestOutcome> annotation : dataModel.annotations.entrySet()){
            String system = annotation.getKey().getSystem();
            String code_id = annotation.getKey().getCode();
            String isNegated = annotation.getValue().isNegated()? "true" : "false";
            String hpo_term = annotation.getValue().getId().getValue();

            Loinc2HpoAnnotationCsvEntry entry = Loinc2HpoAnnotationCsvEntry.Builder.builder()
                    .withLoincId(loincId)
                    .withLoincScale(loincScale)
                    .withSystem(system)
                    .withCode(code_id)
                    .withHpoTermId(hpo_term)
                    .withIsNegated(isNegated)
                    .withCreatedOn(createdOn)
                    .withCreatedBy(createdBy)
                    .withLastEditedOn(lastEditedOn)
                    .withLastEditedBy(lastEditedBy)
                    .withVersion(version)
                    .withIsFinalized(isFinalized)
                    .withComment(comment)
                    .build();

            entries.add(entry);
        }
        return entries;
    }

    public static Map<LoincId, Loinc2HpoAnnotationModel> from_csv(String path) throws IOException, MalformedLoincCodeException {

        List<Loinc2HpoAnnotationCsvEntry> csvEntries = Loinc2HpoAnnotationCsvEntry.importAnnotations(path);

        //organize the TSV entries into data models
        Map<LoincId, Loinc2HpoAnnotationModel> annotationModelMap = new LinkedHashMap<>();

        //go through each entry, create a new data model if it is first seen
        //otherwise, just add more additional information
        for (Loinc2HpoAnnotationCsvEntry entry : csvEntries){
            String loincId_str = entry.getLoincId();
            String loincScale_str = entry.getLoincScale();
            String system = entry.getSystem();
            String code = entry.getCode();
            String hpoTermId_str = entry.getHpoTermId();
            String isNegated_str = entry.getIsNegated();
            String createdOn_str = entry.getCreatedOn();
            String createdBy = entry.getCreatedBy();
            String lastEditedOn_str = entry.getLastEditedOn();
            String lastEditedBy = entry.getLastEditedBy();
            String version = entry.getVersion();
            String isFinalized_str = entry.getIsFinalized();
            String comment = entry.getComment();

            //convert strings to the correct object
            LoincId loincId = new LoincId(loincId_str);
            LoincScale loincScale = LoincScale.string2enum(loincScale_str);
            Code interpretationCode = new Code().setSystem(system).setCode(code);
            HpoTerm4TestOutcome mappedTo = new HpoTerm4TestOutcome(TermId.of(hpoTermId_str),
                    isNegated_str.equals("true"));
            LocalDateTime createdOn = createdOn_str.equals("NA")? null : LocalDateTime.parse(createdOn_str);
            LocalDateTime lastEditedOn = lastEditedOn_str.equals("NA")? null : LocalDateTime.parse(lastEditedOn_str);

            //create a new annotation model if it does not exist for current loincId
            if (!annotationModelMap.containsKey(loincId)){
                Loinc2HpoAnnotationModel newModel = Builder.builder()
                        .withLoincId(loincId)
                        .withLoincScale(loincScale)
                        .withAnnotations(new LinkedHashMap<>()) //fill in data below
                        .withCreatedOn(createdOn)
                        .withCreatedBy(createdBy)
                        .withLastEditedOn(lastEditedOn)
                        .withLastEditedBy(lastEditedBy)
                        .withVersion(version)
                        .withIsFinalized(isFinalized_str.equals("true"))
                        .withComment(comment)
                        .build();
                annotationModelMap.put(loincId, newModel);
            }

            //add annotation data
            annotationModelMap.get(loincId)
                    .getAnnotations()
                    .put(interpretationCode, mappedTo);

        }

        return annotationModelMap;
    }

    public Loinc2HpoAnnotationModel(LoincId loincId, LoincScale loincScale, Map<Code, HpoTerm4TestOutcome> annotations, LocalDateTime createdOn, String createdBy, LocalDateTime lastEditedOn, String lastEditedBy, String version, boolean isFinalized, String comment) {
        this.loincId = loincId;
        this.loincScale = loincScale;
        this.annotations = annotations;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.lastEditedOn = lastEditedOn;
        this.lastEditedBy = lastEditedBy;
        this.version = version;
        this.isFinalized = isFinalized;
        this.comment = comment;
    }



    public LoincId getLoincId() {
        return loincId;
    }

    public void setLoincId(LoincId loincId) {
        this.loincId = loincId;
    }

    public LoincScale getLoincScale() {
        return loincScale;
    }

    public void setLoincScale(LoincScale loincScale) {
        this.loincScale = loincScale;
    }

    public Map<Code, HpoTerm4TestOutcome> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<Code, HpoTerm4TestOutcome> annotations) {
        this.annotations = annotations;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getLastEditedOn() {
        return lastEditedOn;
    }

    public void setLastEditedOn(LocalDateTime lastEditedOn) {
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

    public boolean isFinalized() {
        return isFinalized;
    }

    public void setFinalized(boolean finalized) {
        isFinalized = finalized;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static final class Builder {
        private LoincId loincId;
        private LoincScale loincScale;
        private Map<Code, HpoTerm4TestOutcome> annotations;
        private LocalDateTime createdOn;
        private String createdBy;
        private LocalDateTime lastEditedOn;
        private String lastEditedBy;
        private String version;
        private boolean isFinalized;
        private String comment;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withLoincId(LoincId loincId) {
            this.loincId = loincId;
            return this;
        }

        public Builder withLoincScale(LoincScale loincScale) {
            this.loincScale = loincScale;
            return this;
        }

        public Builder withAnnotations(Map<Code, HpoTerm4TestOutcome> annotations) {
            this.annotations = annotations;
            return this;
        }

        public Builder withCreatedOn(LocalDateTime createdOn) {
            this.createdOn = createdOn;
            return this;
        }

        public Builder withCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder withLastEditedOn(LocalDateTime lastEditedOn) {
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

        public Builder withIsFinalized(boolean isFinalized) {
            this.isFinalized = isFinalized;
            return this;
        }

        public Builder withComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Loinc2HpoAnnotationModel build() {
            return new Loinc2HpoAnnotationModel(loincId, loincScale, annotations, createdOn, createdBy, lastEditedOn, lastEditedBy, version, isFinalized, comment);
        }
    }
}

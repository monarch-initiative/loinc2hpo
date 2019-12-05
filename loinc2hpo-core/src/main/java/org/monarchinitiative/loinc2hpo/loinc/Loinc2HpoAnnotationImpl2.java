package org.monarchinitiative.loinc2hpo.loinc;

import org.monarchinitiative.loinc2hpo.codesystems.Code;

import java.time.LocalDateTime;
import java.util.Map;

public class Loinc2HpoAnnotationImpl2 {

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

    public Loinc2HpoAnnotationImpl2(LoincId loincId, LoincScale loincScale, Map<Code, HpoTerm4TestOutcome> annotations, LocalDateTime createdOn, String createdBy, LocalDateTime lastEditedOn, String lastEditedBy, String version, boolean isFinalized, String comment) {
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
}

package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.monarchinitiative.loinc2hpocore.codesystems.OutcomeCodeOLD;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.io.Loinc2HpoAnnotationParserLEGACY;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpocore.loinc.LoincScale;
import org.monarchinitiative.phenol.ontology.data.TermId;

import javax.validation.constraints.NotNull;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for managing the annotation information for a
 * LOINC coded lab test. In essence, is uses a map to record mappings from an
 * interpretation code to a HPO term (plus a negation boolean value). The key
 * of this map, {@link OutcomeCodeOLD},
 * must have two parts (code system and
 * actual code). We use a set of internal codes for the annotation, which is
 * a subset of FHIR codes:
 *
 * L(ow)                -> Hpo term
 * A(bnormal)/N(ormal)  -> Hpo term
 * H(igh)               -> Hpo term
 * For Ord types with a "Presence" or "Absence" outcome:
 * POS(itive)           -> Hpo term
 * Neg(ative)           -> Hpo term
 *
 * It is also okay to annotate with code from any coding system, for example,
 * one could use SNOMED concepts.
 */
public class Loinc2HpoAnnotationModelLEGACY {

    private static final String MISSINGVALUE = "NA";

    private static final String [] headerFields = {"loincId", "loincScale", "system",
            "code", "hpoTermId", "isNegated", "createdOn", "createdBy",  "lastEditedOn",
            "lastEditedBy", "version", "isFinalized",  "comment"};



    private final LoincId loincId;
    private final LoincScale loincScale;
    private final HashMap<ShortCode, Hpo2Outcome> candidateHpoTerms;
    private final LocalDateTime createdOn;
    private final String createdBy;
    private final LocalDateTime lastEditedOn;
    private final String lastEditedBy;
    private double version;
    private final String note; //any comment for this annotation, say e.g. "highly confident about this annotation"
    private final boolean flag; //a simpler version that equals a comment "not sure about the annotation, come back later"


    public Loinc2HpoAnnotationModelLEGACY(LoincId loincId, LoincScale loincScale,
                                          Map<ShortCode, Hpo2Outcome> annotationMap,
                                          LocalDateTime createdOn, String createdBy,
                                          LocalDateTime lastEditedOn, String lastEditedBy,
                                          String note, boolean flag, double version) {
        this.loincId = loincId;
        this.loincScale = loincScale;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.lastEditedOn = lastEditedOn;
        this.lastEditedBy = lastEditedBy;
        this.note = note;
        this.flag = flag;
        this.version = version;
        this.candidateHpoTerms = new LinkedHashMap<>(annotationMap);
    }


    public static List<Loinc2HpoAnnotationEntryLEGACY> to_csv_entries(Loinc2HpoAnnotationModelLEGACY dataModel){
        String loincId = dataModel.getLoincId().toString();
        String loincScale = dataModel.getLoincScale().toString();
        String createdOn = dataModel.getCreatedOn() == null ? MISSINGVALUE :
                dataModel.getCreatedOn().toString();
        String createdBy = dataModel.getCreatedBy();
        String lastEditedOn = dataModel.getLastEditedOn() == null ? MISSINGVALUE : dataModel.getLastEditedOn().toString();
        String lastEditedBy = dataModel.getLastEditedBy();
        String version = Double.toString(dataModel.getVersion());
        String isFinalized = dataModel.getFlag()? "false" : "true";
        String comment = dataModel.getNote();

        Map<ShortCode, Hpo2Outcome> annotations = dataModel.getCandidateHpoTerms();

        List<Loinc2HpoAnnotationEntryLEGACY> entries = new ArrayList<>();
        for (var annotation: annotations.entrySet()){
            //skip record if the mapped term is null
            if (annotation.getValue() == null ||
                    annotation.getValue().getId() == null){
                continue;
            }
            String system = "TODO";
            ShortCode code_id = annotation.getKey();
            String isNegated = annotation.getValue().isNegated()? "true" : "false";
            String hpo_term = annotation.getValue().getId().getValue();

            Loinc2HpoAnnotationEntryLEGACY entry = Loinc2HpoAnnotationEntryLEGACY.of(
                    loincId, loincScale, system, code_id, hpo_term, isNegated, createdOn,
                            createdBy, lastEditedOn, lastEditedBy, version,isFinalized,comment);

            entries.add(entry);
        }
        return entries;
    }

    public static void to_csv_file(Map<LoincId, Loinc2HpoAnnotationModelLEGACY> annotationMap, String file_path) throws IOException{
        String header = String.join("\t", headerFields);
        List<String> lines_to_write = annotationMap.values().stream()
                .map(Loinc2HpoAnnotationModelLEGACY::to_csv_entries)
                .flatMap(Collection::stream)
                .map(Loinc2HpoAnnotationEntryLEGACY::toString)
                .map(String::trim)
                .collect(Collectors.toList());
        lines_to_write.add(0, header);
        String content = String.join("\n", lines_to_write);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file_path));
        writer.write(content);
        writer.close();
    }

    public static Map<LoincId, Loinc2HpoAnnotationModelLEGACY> from_csv(String path) {

        List<Loinc2HpoAnnotationEntryLEGACY> csvEntries = Loinc2HpoAnnotationParserLEGACY.load(path);

        //organize the TSV entries into data models
        Map<LoincId, Loinc2HpoAnnotationModelLEGACY> annotationModelMap = new LinkedHashMap<>();

        //go through each entry, create a new data model if it is first seen
        //otherwise, just add more additional information
        for (Loinc2HpoAnnotationEntryLEGACY entry : csvEntries){
            String loincId_str = entry.getLoincId();
            String loincScale_str = entry.getLoincScale();
            ShortCode outcomeCode = entry.getCode();
            String hpoTermId_str = entry.getHpoTermId();
            String isNegated_str = entry.getIsNegated();
            String createdOn_str = entry.getCreatedOn();
            String createdBy = entry.getCreatedBy();
            String lastEditedOn_str = entry.getLastEditedOn();
            String lastEditedBy = entry.getLastEditedBy();
            double version = Double.parseDouble(entry.getVersion());
            String isFinalized_str = entry.getIsFinalized();
            String comment = entry.getComment();

            //convert strings to the correct object
            LoincId loincId = new LoincId(loincId_str);
            LoincScale loincScale = LoincScale.string2enum(loincScale_str);
//            Hpo2Outcome mappedTo = new Hpo2Outcome(TermId.of(hpoTermId_str),
//                    isNegated_str.equals("true"));
            Hpo2Outcome mappedTo = new Hpo2Outcome(TermId.of(hpoTermId_str),
                    ShortCode.U);
            LocalDateTime createdOn = createdOn_str == null? null :
                    LocalDateTime.parse(createdOn_str);
            LocalDateTime lastEditedOn = lastEditedOn_str == null? null :
                    LocalDateTime.parse(lastEditedOn_str);

            //create a new annotation model if it does not exist for current loincId
            if (!annotationModelMap.containsKey(loincId)){
                Loinc2HpoAnnotationModelLEGACY newModel =
                        new Loinc2HpoAnnotationModelLEGACY.Builder()
                        .setLoincId(loincId)
                        .setLoincScale(loincScale)
                        .setCreatedOn(createdOn)
                        .setCreatedBy(createdBy)
                        .setLastEditedOn(lastEditedOn)
                        .setLastEditedBy(lastEditedBy)
                        .setVersion(version)
                        .setFlag(isFinalized_str.equals("false"))
                        .setNote(comment)
                        .build();
                annotationModelMap.put(loincId, newModel);
            }
            annotationModelMap.get(loincId)
                    .getCandidateHpoTerms()
                    .put(outcomeCode, mappedTo);

        }

        return annotationModelMap;
    }



    public LoincId getLoincId(){ return this.loincId; }

    public LoincScale getLoincScale() { return this.loincScale;}

    public  String getNote(){
        return this.note;
    }

    public boolean getFlag(){
        return this.flag;
    }

    public double getVersion() {
        return version;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getLastEditedOn() {
        return lastEditedOn;
    }

    public String getLastEditedBy() {
        return lastEditedBy;
    }


    /**
     * Get the corresponding Hpo term for a coded value
     * @param code a code in a coding system. Usually, it is the internal code; for Ord, Nom, or Nar, it can be codes of
     *             an external coding system
     * @return the hpo term wrapped in the HpoTerm4TestOutcome class
     */
    public Hpo2Outcome loincInterpretationToHPO(ShortCode code) {
        return candidateHpoTerms.get(code);
    }


    /**
     * Return the annotation map.
     * @TODO: consider return a copy of the map, but it will affect the parsing method.
     * @return
     */
    public HashMap<ShortCode, Hpo2Outcome> getCandidateHpoTerms() {
        return candidateHpoTerms;
    }


    @Override
    /**
     * The default toString method will serialize all the annotations to a string
     */
    public String toString(){

        StringBuilder stringBuilder = new StringBuilder();
        candidateHpoTerms.forEach((code, hpoTermId4LoincTest) -> {
            stringBuilder.append(this.loincId);
            stringBuilder.append("\t").append(this.loincScale.toString());
            stringBuilder.append("\t" + code.shortForm());
            stringBuilder.append("\t" + hpoTermId4LoincTest.getId().getValue());
            stringBuilder.append("\t" + hpoTermId4LoincTest.isNegated());
            stringBuilder.append("\t" + this.note);
            stringBuilder.append("\t" + this.flag);
            stringBuilder.append("\t" + String.format("%.1f", this.version));
            stringBuilder.append("\t" + (this.createdOn == null ? MISSINGVALUE : this.createdOn));
            stringBuilder.append("\t" + (this.createdBy == null ? MISSINGVALUE : this.createdBy));
            stringBuilder.append("\t" + (this.lastEditedOn == null ? MISSINGVALUE : this.lastEditedOn));
            stringBuilder.append("\t" + (this.lastEditedBy == null ? MISSINGVALUE : this.lastEditedBy));
            stringBuilder.append("\n");
        });
        return stringBuilder.toString().trim();

    }

    // The following define some convenient methods
    // TODO: consider moving them out
    /**
     * A convenient method to show hpo term for low
     * @return
     */
    public TermId whenValueLow() {
        if (loincInterpretationToHPO(ShortCode.L) != null) {
            return loincInterpretationToHPO(ShortCode.L).getId();
        } else {
            return null;
        }
    }

    /**
     * A convenient method to show hpo term for normal (Qn) or negative (Ord)
     * @return
     */
    public TermId whenValueNormalOrNegative() {

        if (loincInterpretationToHPO(ShortCode.N) != null) {
            return loincInterpretationToHPO(ShortCode.N).getId();
        } else if (loincInterpretationToHPO(ShortCode.ABSENT) != null) {
            return loincInterpretationToHPO(ShortCode.ABSENT).getId();
        } else {
            return null;
        }
    }


    /**
     * A convenient method to show hpo term for high (Qn) or positive (Ord)
     * @return
     */
    public TermId whenValueHighOrPositive() {

        if (loincInterpretationToHPO(ShortCode.H) != null) {
            return loincInterpretationToHPO(ShortCode.H).getId();
        } else if (loincInterpretationToHPO(ShortCode.PRESENT) != null) {
            return loincInterpretationToHPO(ShortCode.PRESENT).getId();
        } else {
            return null;
        }

    }


    public static class Builder {

        private LoincId loincId = null;
        private LoincScale loincScale = null;
        private Map<ShortCode, Hpo2Outcome> annotationMap = new LinkedHashMap<>();
        private LocalDateTime createdOn = null;
        private String createdBy = null;
        private LocalDateTime lastEditedOn = null;
        private String lastEditedBy = null;
        private String note = null;
        private boolean flag = false;
        private double version = 0.0;

        public Builder() {

        }

        /**
         * Specify the LOINC Id (<7 number separated by "-")
         * @param loincId
         */
        public Builder setLoincId(LoincId loincId) {
            this.loincId = loincId;
            return this;
        }

        /**
         * Set the LOINC scale
         * @param loincScale
         */
        public Builder setLoincScale(LoincScale loincScale) {
            this.loincScale = loincScale;
            return this;
        }

        /**
         * Set the HPO term when the measured value is low
         * @param low
         */
        public Builder setLowValueHpoTerm(@NotNull TermId low) {
            return addAnnotation(ShortCode.L, new Hpo2Outcome(low,
                    ShortCode.L));
        }

        /**
         * Set the HPO term when the measured value is intermediate (typically "normal")
         * @param intermediate
         */
        public Builder setIntermediateValueHpoTerm(@NotNull TermId intermediate, boolean isNegated) {
            return addAnnotation(ShortCode.N,
                    new Hpo2Outcome(intermediate, ShortCode.N));
        }

        /**
         * Specify the HPO term when the measured value is high.
         * @param high
         */
        public Builder setHighValueHpoTerm(@NotNull TermId high) {
            return addAnnotation(ShortCode.H, new Hpo2Outcome(high, ShortCode.H));
        }

        public Builder setPosValueHpoTerm(@NotNull TermId pos) {
            return addAnnotation(ShortCode.PRESENT, new Hpo2Outcome(pos, ShortCode.PRESENT));
        }

        public Builder setNegValueHpoTerm(@NotNull TermId neg,
                                          boolean inverse) {
            return addAnnotation(ShortCode.ABSENT, new Hpo2Outcome(neg, ShortCode.A));
        }

        /**
         * Add an annotation.
         * @param code
         * @param annotation
         * @return
         */
        public Builder addAnnotation(ShortCode code, Hpo2Outcome annotation) {
            this.annotationMap.put(code, annotation);
            return this;
        }

        /**
         * Set Date and Time when a LOINC annotation was created.
         * @param createdOn
         */
        public Builder setCreatedOn(LocalDateTime createdOn) {
            this.createdOn = createdOn;
            return this;
        }

        /**
         * Set the biocurator who created the LOINC annotation
         * @param biocurator
         */
        public Builder setCreatedBy(String biocurator) {
            this.createdBy = biocurator;
            return this;
        }

        /**
         * Set Date and Time when a LOINC annotation is updated.
         * @param lastEditedOn
         */
        public Builder setLastEditedOn(LocalDateTime lastEditedOn) {
            this.lastEditedOn = lastEditedOn;
            return this;
        }

        /**
         * Set the biocurator who edited a LOINC annotation
         * @param biocurator
         */
        public Builder setLastEditedBy(String biocurator) {
            this.lastEditedBy = biocurator;
            return this;
        }

        /**
         * Add the note for the annotation.
         * @param note something that explains the quality or reasoning behind the annotation
         * @return the annotation object
         */
        public Builder setNote(String note) {
            this.note = note;
            return this;
        }

        /**
         * Add a flag for the annotation
         * param flag if true, it means that annotation is worth a re-visit
         * @return the annotation object
         */
        public Builder setFlag(boolean flag) {
            this.flag = flag;
            return this;
        }

        /**
         * Initial version number is 0.1. Increment by 0.1 every time it is updated.
         * @param version
         * @return
         */
        public Builder setVersion(double version) {
            this.version = version;
            return this;
        }

        public Loinc2HpoAnnotationModelLEGACY build() {
            return new Loinc2HpoAnnotationModelLEGACY(loincId,
                    loincScale,
                    annotationMap,
                    createdOn,
                    createdBy,
                    lastEditedOn,
                    lastEditedBy,
                    note,
                    flag,
                    version);
        }
    }

}

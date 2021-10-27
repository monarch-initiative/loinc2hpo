package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.apache.commons.lang3.StringUtils;
import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.codesystems.InternalCode;
import org.monarchinitiative.loinc2hpocore.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpocore.codesystems.InternalCodeSystem;
import org.monarchinitiative.loinc2hpocore.io.Loinc2HpoAnnotationParser;
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
 * of this map, {@link org.monarchinitiative.loinc2hpocore.codesystems.Code},
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
public class Loinc2HpoAnnotationModel {

    private static final String MISSINGVALUE = "NA";

    public static String csv_header(String delim){

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
        String createdOn = dataModel.getCreatedOn() == null ? MISSINGVALUE :
                dataModel.getCreatedOn().toString();
        String createdBy = dataModel.getCreatedBy();
        String lastEditedOn = dataModel.getLastEditedOn() == null ? MISSINGVALUE : dataModel.getLastEditedOn().toString();
        String lastEditedBy = dataModel.getLastEditedBy();
        String version = Double.toString(dataModel.getVersion());
        String isFinalized = dataModel.getFlag()? "false" : "true";
        String comment = dataModel.getNote();

        List<Loinc2HpoAnnotationCsvEntry> entries = new ArrayList<>();
        for (Map.Entry<Code, HpoTerm4TestOutcome> annotation :
                dataModel.getCandidateHpoTerms().entrySet()){
            //skip record if the mapped term is null
            if (annotation.getValue() == null ||
                    annotation.getValue().getId() == null){
                continue;
            }
            String system = annotation.getKey().getSystem();
            String code_id = annotation.getKey().getCode();
            String isNegated = annotation.getValue().isNegated()? "true" : "false";
            String hpo_term = annotation.getValue().getId().getValue();

            Loinc2HpoAnnotationCsvEntry entry = Loinc2HpoAnnotationCsvEntry.of(
                    loincId,loincScale, system, code_id, hpo_term, isNegated, createdOn,
                            createdBy, lastEditedOn, lastEditedBy, version,isFinalized,comment);

            entries.add(entry);
        }
        return entries;
    }

    public static void to_csv_file(Map<LoincId, Loinc2HpoAnnotationModel> annotationMap, String file_path) throws IOException{
        String header = Loinc2HpoAnnotationModel.csv_header("\t");
        List<String> lines_to_write = annotationMap.values().stream()
                .map(Loinc2HpoAnnotationModel::to_csv_entries)
                .flatMap(Collection::stream)
                .map(Loinc2HpoAnnotationCsvEntry::toString)
                .map(String::trim)
                .collect(Collectors.toList());
        lines_to_write.add(0, header);
        String content = StringUtils.join(lines_to_write, "\n");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file_path));
        writer.write(content);
        writer.close();
    }

    public static Map<LoincId, Loinc2HpoAnnotationModel> from_csv(String path) throws MalformedLoincCodeException {

        List<Loinc2HpoAnnotationCsvEntry> csvEntries = Loinc2HpoAnnotationParser.load(path);

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
            double version = Double.parseDouble(entry.getVersion());
            String isFinalized_str = entry.getIsFinalized();
            String comment = entry.getComment();

            //convert strings to the correct object
            LoincId loincId = new LoincId(loincId_str);
            LoincScale loincScale = LoincScale.string2enum(loincScale_str);
            Code interpretationCode = new Code().setSystem(system).setCode(code);
            HpoTerm4TestOutcome mappedTo = new HpoTerm4TestOutcome(TermId.of(hpoTermId_str),
                    isNegated_str.equals("true"));
            LocalDateTime createdOn = createdOn_str == null? null :
                    LocalDateTime.parse(createdOn_str);
            LocalDateTime lastEditedOn = lastEditedOn_str == null? null :
                    LocalDateTime.parse(lastEditedOn_str);

            //create a new annotation model if it does not exist for current loincId
            if (!annotationModelMap.containsKey(loincId)){
                Loinc2HpoAnnotationModel newModel =
                        new Loinc2HpoAnnotationModel.Builder()
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

            //add annotation data
            annotationModelMap.get(loincId)
                    .getCandidateHpoTerms()
                    .put(interpretationCode, mappedTo);

        }

        return annotationModelMap;
    }

    private final LoincId loincId;
    private final LoincScale loincScale;
    private final HashMap<Code, HpoTerm4TestOutcome> candidateHpoTerms;
    private final LocalDateTime createdOn;
    private final String createdBy;
    private final LocalDateTime lastEditedOn;
    private final String lastEditedBy;
    private double version;
    private final String note; //any comment for this annotation, say e.g. "highly confident about this annotation"
    private final boolean flag; //a simpler version that equals a comment "not sure about the annotation, come back later"


    public Loinc2HpoAnnotationModel(LoincId loincId, LoincScale loincScale,
                                    Map<Code, HpoTerm4TestOutcome> annotationMap,
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

        //put other terms into the map
        this.candidateHpoTerms = new LinkedHashMap<>(annotationMap);
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

    public Loinc2HpoAnnotationModel setVersion(double version) {
        this.version = version;
        return this;
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
    public HpoTerm4TestOutcome loincInterpretationToHPO(Code code) {
        return candidateHpoTerms.get(code);
    }


    /**
     * Return the annotation map.
     * @TODO: consider return a copy of the map, but it will affect the parsing method.
     * @return
     */
    public HashMap<Code, HpoTerm4TestOutcome> getCandidateHpoTerms() {
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
            stringBuilder.append("\t" + this.loincScale.toString());
            stringBuilder.append("\t" + code.getSystem());
            stringBuilder.append("\t" + code.getCode());
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

        if (loincInterpretationToHPO(InternalCodeSystem.getCode(InternalCode.L)) != null) {
            return loincInterpretationToHPO(InternalCodeSystem.getCode(InternalCode.L)).getId();
        } else {
            return null;
        }
    }

    /**
     * A convenient method to show hpo term for normal (Qn) or negative (Ord)
     * @return
     */
    public TermId whenValueNormalOrNegative() {

        if (loincInterpretationToHPO(InternalCodeSystem.getCode(InternalCode.N)) != null) {
            return loincInterpretationToHPO(InternalCodeSystem.getCode(InternalCode.N)).getId();
        } else if (loincInterpretationToHPO(InternalCodeSystem.getCode(InternalCode.NEG)) != null) {
            return loincInterpretationToHPO(InternalCodeSystem.getCode(InternalCode.NEG)).getId();
        } else {
            return null;
        }

    }


    /**
     * A convenient method to show hpo term for high (Qn) or positive (Ord)
     * @return
     */
    public TermId whenValueHighOrPositive() {

        if (loincInterpretationToHPO(InternalCodeSystem.getCode(InternalCode.H)) != null) {
            return loincInterpretationToHPO(InternalCodeSystem.getCode(InternalCode.H)).getId();
        } else if (loincInterpretationToHPO(InternalCodeSystem.getCode(InternalCode.POS)) != null) {
            return loincInterpretationToHPO(InternalCodeSystem.getCode(InternalCode.POS)).getId();
        } else {
            return null;
        }

    }


    public static class Builder {

        private LoincId loincId = null;
        private LoincScale loincScale = null;
        private Map<Code, HpoTerm4TestOutcome> annotationMap = new LinkedHashMap<>();
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

            Code internalLow = InternalCodeSystem.getCode(InternalCode.L);
            return addAnnotation(internalLow, new HpoTerm4TestOutcome(low,
                    false));

        }

        /**
         * Set the HPO term when the measured value is intermediate (typically "normal")
         * @param intermediate
         */
        public Builder setIntermediateValueHpoTerm(@NotNull TermId intermediate, boolean isNegated) {

            Code internalNormal = InternalCodeSystem.getCode(InternalCode.N);
            return addAnnotation(internalNormal,
                    new HpoTerm4TestOutcome(intermediate, isNegated));

        }

        /**
         * Specify the HPO term when the measured value is high.
         * @param high
         */
        public Builder setHighValueHpoTerm(@NotNull TermId high) {

            Code internalHigh = InternalCodeSystem.getCode(InternalCode.H);
            return addAnnotation(internalHigh, new HpoTerm4TestOutcome(high,
                    false));

        }

        public Builder setPosValueHpoTerm(@NotNull TermId pos) {

            Code internalPos = InternalCodeSystem.getCode(InternalCode.POS);
            return addAnnotation(internalPos, new HpoTerm4TestOutcome(pos,
                    false));

        }

        public Builder setNegValueHpoTerm(@NotNull TermId neg,
                                          boolean inverse) {

            Code internalNeg = InternalCodeSystem.getCode(InternalCode.NEG);
            return addAnnotation(internalNeg, new HpoTerm4TestOutcome(neg,
                    inverse));

        }

        /**
         * Add an annotation.
         * @param code
         * @param annotation
         * @return
         */
        public Builder addAnnotation(Code code, HpoTerm4TestOutcome annotation) {

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

        public Loinc2HpoAnnotationModel build() {

            return new Loinc2HpoAnnotationModel(loincId,
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

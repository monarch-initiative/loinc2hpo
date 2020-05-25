package org.monarchinitiative.loinc2hpocore.loinc;

import org.monarchinitiative.loinc2hpocore.codesystems.Code;
import org.monarchinitiative.loinc2hpocore.codesystems.InternalCode;
import org.monarchinitiative.loinc2hpocore.exception.UnrecognizedCodeException;
import org.monarchinitiative.loinc2hpocore.codesystems.InternalCodeSystem;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * This class is responsible for managing the annotation information. The app keeps a map from loinc -> annotation. This
 * class is only the annotation part. For each loinc code, we assign a candidate Hpo term for a potential observation
 * value. The observation value is a code in a coding system (if it is a numeric value, we change it to a code). For Qn
 * type of Loinc, we use the internal codes, which is a subset of FHIR codes:
 * L(ow)                -> Hpo term
 * A(bnormal)/N(ormal)  -> Hpo term
 * H(igh)               -> Hpo term
 * For Ord types with a "Presence" or "Absence" outcome:
 * POS(itive)           -> Hpo term
 * Neg(ative)           -> Hpo term
 *
 * For Ord, Nom and other types, the observation is always a code in an external coding system, we have to assign Hpo
 * terms to expected coded values, or we have to convert the external coded value to an internal coded value listed above.
 *
 * Generally, we first consider the interpretation field and try to utilize this information so that we can avoid the
 * necessity to map various coding systems. We have a built-in map that converts fhir interpretation codes to our internal
 * code. We still need to have more maps for different interpretation systems.
 */
public class LOINC2HpoAnnotationImpl implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String MISSINGVALUE = "NA";

    private double version = 0.0;
    private LocalDateTime createdOn = null;
    private String createdBy = null;
    private LocalDateTime lastEditedOn = null;
    private String lastEditedBy = null;
    private LoincId loincId = null;
    private LoincScale loincScale = null;
    private HashMap<Code, HpoTerm4TestOutcome> candidateHpoTerms;
    private String note = null; //any comment for this annotation, say e.g. "highly confident about this annotation"
    private boolean flag = false; //a simpler version that equals a comment "not sure about the annotation, come back later"


    public LOINC2HpoAnnotationImpl(LoincId loincId, LoincScale loincScale,
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

    private LOINC2HpoAnnotationImpl() {

    }

    public static String getHeader() {
        String header = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                "loincId", "loincScale", "system", "code", "hpoTermId", "inversed", "note", "flag",
                "version", "createdOn", "createdBy", "lastEditedOn", "lastEditedBy");
        return header;
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

    public LOINC2HpoAnnotationImpl setVersion(double version) {
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




    // The following define some helper functions for GUI
    // TODO: consider moving them out
    private TermId getHpoTermIdForInternalCode(String code){
        try {
            InternalCode internalCode = InternalCode.fromCode(code);
            return candidateHpoTerms.get(InternalCodeSystem.getCode(internalCode)).getId();
        } catch (UnrecognizedCodeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public  TermId getBelowNormalHpoTermId(){

        return getHpoTermIdForInternalCode("L");
        //return this.low == null ? null : this.low.getId();
    }
    //@Override

    public  TermId getNotAbnormalHpoTermName(){

        return getHpoTermIdForInternalCode("N");
        //return this.intermediate == null ? null : this.intermediate.getId();
    }
    //@Override

    public TermId getAbnormalHpoTermName() {

        return getHpoTermIdForInternalCode("A");
    }
    //@Override

    public  TermId getAboveNormalHpoTermName(){

        return getHpoTermIdForInternalCode("H");
        //return this.high == null ? null : this.high.getId();
    }

    public TermId getNegativeHpoTermName() {

        return getHpoTermIdForInternalCode("NEG");

    }

    public TermId getPositiveHpoTermName() {

        return getHpoTermIdForInternalCode("POS");

    }

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

    public boolean isNormalOrNegativeInversed() {
        if (loincInterpretationToHPO(InternalCodeSystem.getCode(InternalCode.N)) != null) {
            return loincInterpretationToHPO(InternalCodeSystem.getCode(InternalCode.N)).isNegated();
        } else if (loincInterpretationToHPO(InternalCodeSystem.getCode(InternalCode.NEG)) != null) {
            return loincInterpretationToHPO(InternalCodeSystem.getCode(InternalCode.NEG)).isNegated();
        } else {
            return false;
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

    public boolean hasCreatedOn() {

        return this.createdOn != null;

    }

    public boolean hasCreatedBy() {

        return this.createdBy != null;

    }

    public boolean hasLastEditedOn() {

        return this.lastEditedOn != null;

    }

    public boolean hasLastEditedBy() {

        return this.lastEditedBy != null;

    }

    public boolean hasComment() {

        return this.note != null && !this.note.trim().isEmpty();

    }

    /**
     *
     * @return
     */
    public Set<Code> getCodes(){
        return candidateHpoTerms.keySet();
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


    public HashMap<Code, HpoTerm4TestOutcome> getCandidateHpoTerms() {
        return new HashMap<>(candidateHpoTerms);
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


    public static class Builder {

        private LoincId loincId = null;
        private LoincScale loincScale = null;
        private TermId low = null;
        private TermId intermediate = null;
        private boolean intermediateNegated = false;
        private TermId high = null;
        private Map<Code, HpoTerm4TestOutcome> annotationMap = new HashMap<>();
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
        public Builder setLowValueHpoTerm(TermId low) {

            this.low = low;
            if (low == null) {
                return this;
            }
            annotationMap.put(InternalCodeSystem.getCode(InternalCode.L),
                        new HpoTerm4TestOutcome(low, false));
            return this;

        }

        /**
         * Set the HPO term when the measured value is intermediate (typically "normal")
         * @param intermediate
         */
        public Builder setIntermediateValueHpoTerm(TermId intermediate, boolean isNegated) {

            this.intermediate = intermediate;
            if (intermediate == null) {
                return this;
            }
            //use the default value for negated, which will be overwritten when user provides real value
            annotationMap.put(InternalCodeSystem.getCode(InternalCode.N),
                    new HpoTerm4TestOutcome(intermediate, isNegated));
            annotationMap.put(InternalCodeSystem.getCode(InternalCode.A),
                    new HpoTerm4TestOutcome(intermediate, !isNegated));
            return this;

        }

        /**
         * Specify the HPO term when the measured value is high.
         * @param high
         */
        public Builder setHighValueHpoTerm(TermId high) {

            this.high = high;
            if (high == null) {
                return this;
            }
            annotationMap.put(InternalCodeSystem.getCode(InternalCode.H),
                    new HpoTerm4TestOutcome(high, false));
            return this;

        }

        public Builder setPosValueHpoTerm(TermId pos) {

            if (pos == null) {
                return this;
            }

            annotationMap.put(InternalCodeSystem.getCode(InternalCode.POS),
                    new HpoTerm4TestOutcome(pos, false));
            return this;
        }

        public Builder setNegValueHpoTerm(TermId neg, boolean inverse) {

            if (neg == null) {
                return this;
            }

            annotationMap.put(InternalCodeSystem.getCode(InternalCode.NEG),
                    new HpoTerm4TestOutcome(neg, inverse));
            return this;
        }

        /**
         * Add an annotation in the advanced mode.
         * @param code
         * @param annotation
         * @return
         */
        public Builder addAdvancedAnnotation(Code code, HpoTerm4TestOutcome annotation) {

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

        public LOINC2HpoAnnotationImpl build() {

            return new LOINC2HpoAnnotationImpl(loincId,
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

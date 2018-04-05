package org.monarchinitiative.loinc2hpo.loinc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.ontology.data.TermId;
import org.apache.maven.model.Build;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * TODO: implement Json serialization
 * This class is responsible for managing the annotation information. The app keeps a map from loinc -> annotation. This
 * class is only the annotation part. For each loinc code, we assign a candidate Hpo term for a potential observation
 * value. The observation value is a code in a coding system (if it is a numeric value, we change it to a code). For Qn
 * type of Loinc, we use the internal code:
 * L(ow)                -> Hpo term
 * A(bnormal)/N(ormal)  -> Hpo term
 * H(igh)               -> Hpo term
 * P(ositive)           -> Hpo term
 * N(ot)P(ositive)      -> Hpo term
 *
 * For Ord, Nom and other types, the observation is always a code in an external coding system, we have to assign Hpo
 * terms to expected coded values, or we have to convert the external coded value to an internal coded value listed above.
 *
 * Generally, we first consider the interpretation field and try to utilize this information so that we can avoid the
 * necessity to map various coding systems. We have a built-in map that converts fhir interpretation codes to our internal
 * code. We still need to have more maps for different interpretation systems.
 */

@JsonIgnoreProperties({"codeSystems", "unrecognizedCodes"})
//@JsonInclude(JsonInclude.Include.NON_NULL.NON_EMPTY)
public class UniversalLoinc2HPOAnnotation implements Serializable {

    private static Map<String, Code> internalCode = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);

    @JsonIgnore
    private static final long serialVersionUID = 1L;
    private static final String MISSINGVALUE = "NA";

    @JsonProperty("version")
    private double version = 0.0;
    private LocalDateTime createdOn = null;
    private String createdBy = null;
    private LocalDateTime lastEditedOn = null;
    private String lastEditedBy = null;

    @JsonProperty("loinc id")
    private LoincId loincId = null;
    @JsonProperty("loinc scale")
    private LoincScale loincScale = null;
    //The following fields record three terms for basic annotations
    private HpoTerm low = null;
    private HpoTerm intermediate = null;
    private boolean intermediateNegated = false;
    private HpoTerm high = null;

    //The following hashmap stores all manually created advanced annotations
    private Map<Code, HpoTermId4LoincTest> advancedAnnotationTerms = null;

    //The following hashmap combines terms for internal codes and advanced annotations.
    //We convert basic annotations to internal codes by a default rule:
    //internal "L" -- low
    //internal "N", "NP" -- intermediate
    //internal "H", "P" -- high
    //internal "A" --intermediate
    //the keys are internal codes; each one should correspond to one HpoTerm4LoincTest
    //alternatively, the codes can be external codes, if it is for a Ord or Nom loinc test
    //access the codes from the CodeSystemConvertor.getCodeContainer
    @JsonProperty("annotations")
    private HashMap<Code, HpoTermId4LoincTest> candidateHpoTerms = new HashMap<>();
    @JsonProperty("note")
    private String note = null; //any comment for this annotation, say e.g. "highly confident about this annotation"
    @JsonProperty("flag")
    private boolean flag = false; //a simpler version that equals a comment "not sure about the annotation, come back later"

    private Set<String> codeSystems; //what code systems are used for annotation
    private Set<Code> unrecognizedCodes; //keep a record if a code is not annotated but used in real-world observation

    /**
     * The default constructor for using the builder to build a class
     * @param loincId
     * @param loincScale
     * @param low
     * @param intermediate
     * @param intermediateNegated
     * @param high
     * @param advancedAnnotationTerms
     * @param createdOn
     * @param createdBy
     * @param lastEditedOn
     * @param lastEditedBy
     * @param note
     * @param flag
     * @param version
     */
    public UniversalLoinc2HPOAnnotation(LoincId loincId,  LoincScale loincScale,
            HpoTerm low,  HpoTerm intermediate,  boolean intermediateNegated,  HpoTerm high,  Map<Code, HpoTermId4LoincTest> advancedAnnotationTerms,  LocalDateTime createdOn,  String createdBy,  LocalDateTime lastEditedOn,  String lastEditedBy,  String note,  boolean flag, double version) {

        this.loincId = loincId;
        this.loincScale = loincScale;
        this.low = low;
        this.intermediate = intermediate;
        this.intermediateNegated = intermediateNegated;
        this.high = high;
        this.advancedAnnotationTerms = advancedAnnotationTerms;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.lastEditedOn = lastEditedOn;
        this.lastEditedBy = lastEditedBy;
        this.note = note;
        this.flag = flag;
        this.version = version;

        //map basic annotations to internal codes;
        //combine with advanced annotations
        //mapToInternalCodes();
        //put all advanced codes into the combined map
        //if an internal code is manually annotated, it will overwrite default map
        this.candidateHpoTerms.putAll(advancedAnnotationTerms);



        //if "A" is not specified, use "N" but negation should be reversed
        //e.g. "N" is NOT abnormal glucose concentration, "A" should be abnormal glucose concentration
        Map<String, Code> internalCode = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);
        if (getAbnormalHpoTermName() == null && getNotAbnormalHpoTermName() != null) {
            HpoTermId4LoincTest normal = candidateHpoTerms.get(internalCode.get("N"));
            candidateHpoTerms.put(internalCode.get("A"), new HpoTermId4LoincTest(normal.getHpoTerm(), !normal.isNegated()));
        }
    }


    @Deprecated
    public UniversalLoinc2HPOAnnotation(LoincId lid, LoincScale lsc){

        //super(lid, lsc);
        this.loincId = lid;
        this.loincScale = lsc;

    }

    @Deprecated
    public UniversalLoinc2HPOAnnotation(LoincId lid) {
        this.loincId = lid;
        //loincScale can be found from the loinc map
    }

    private UniversalLoinc2HPOAnnotation() {

    }

    public static String getHeaderAdvanced() {
        String header = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                "loincId", "loincScale", "system", "code", "hpoTermId", "inversed", "note", "flag",
                "version", "createdOn", "createdBy", "lastEditedOn", "lastEditedBy");
        return header;
    }

    public static String getHeaderBasic() {
        String header = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                "loincId", "loincScale", "hpoLo", "hpoN", "hpoHi", "inversed", "note", "flag", "version", "createdOn", "createdBy", "lastEditedOn", "lastEditedBy");
        return header;
    }

    //map basic annotations to internal codes;
    //combine with advanced annotations
    private void mapToInternalCodes(){


        //We convert basic annotations to internal codes by a default rule:
        //for Qn:
        //internal "L" -- low
        //internal "N" -- intermediate
        //internal "H" -- high
        //internal "A" --intermediate
        //for Ord:
        //internal "NP" -- intermediate
        //internal "P" -- high
        if (low != null) {
            candidateHpoTerms.put(internalCode.get("L"),
                    new HpoTermId4LoincTest(low, false));
        }
        if (intermediate != null) {
            candidateHpoTerms.put(internalCode.get("N"),
                    new HpoTermId4LoincTest(intermediate, intermediateNegated));
            candidateHpoTerms.put(internalCode.get("NP"),
                    new HpoTermId4LoincTest(intermediate, intermediateNegated));
            candidateHpoTerms.put(internalCode.get("A"),
                    new HpoTermId4LoincTest(intermediate, !intermediateNegated));
        }
        if (high != null) {
            if (loincScale == LoincScale.Ord) {
                candidateHpoTerms.put(internalCode.get("P"),
                        new HpoTermId4LoincTest(high, false));
            }
            candidateHpoTerms.put(internalCode.get("H"),
                    new HpoTermId4LoincTest(high, false));

        }

    }


    /**
     * Method to add a annotation in the advanced mode
     * @param code
     * @param hpoTermId4LoincTest
     */
    public void addAdvancedAnnotation(Code code, HpoTermId4LoincTest hpoTermId4LoincTest) {
        this.advancedAnnotationTerms.put(code, hpoTermId4LoincTest);
        this.candidateHpoTerms.put(code, hpoTermId4LoincTest);

    }

    public Map<Code, HpoTermId4LoincTest> getAdvancedAnnotationTerms() {

        return new LinkedHashMap<>(this.advancedAnnotationTerms);

    }


    public LoincId getLoincId(){ return this.loincId; }



    public LoincScale getLoincScale() { return this.loincScale;}




    //@Override
    public  String getNote(){
        return this.note;
    }

    //@Override
    public boolean getFlag(){
        return this.flag;
    }
    public double getVersion() {
        return version;
    }

    public UniversalLoinc2HPOAnnotation setVersion(double version) {
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


    private TermId getHpoTermIdForInternalCode(String internalCode){
        Code code = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM).get(internalCode);
        if (candidateHpoTerms.get(code) == null) {
            return null;
        } else {
            return candidateHpoTerms.get(code).getId();
        }

    }


    //@Override

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

        return getHpoTermIdForInternalCode("NP");

    }

    public TermId getPositiveHpoTermName() {

        return getHpoTermIdForInternalCode("P");

    }

    /**
     * A convenient method to show hpo term for low
     * @return
     */
    public HpoTerm displayLow() {

        if (loincInterpretationToHPO(internalCode.get("L")) != null) {
            return loincInterpretationToHPO(internalCode.get("L")).getHpoTerm();
        } else {
            return null;
        }


    }

    /**
     * A convenient method to show hpo term for normal (Qn) or negative (Ord)
     * @return
     */
    public HpoTerm displayNormal() {

        if (loincInterpretationToHPO(internalCode.get("N")) != null) {
            return loincInterpretationToHPO(internalCode.get("N")).getHpoTerm();
        } else if (loincInterpretationToHPO(internalCode.get("NP")) != null) {
            return loincInterpretationToHPO(internalCode.get("NP")).getHpoTerm();
        } else {
            return null;
        }

    }

    /**
     * A convenient method to show hpo term for high (Qn) or positive (Ord)
     * @return
     */
    public HpoTerm displayHigh() {

        if (loincInterpretationToHPO(internalCode.get("H")) != null) {
            return loincInterpretationToHPO(internalCode.get("H")).getHpoTerm();
        } else if (loincInterpretationToHPO(internalCode.get("P")) != null) {
            return loincInterpretationToHPO(internalCode.get("P")).getHpoTerm();
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
     * When we run the software to parse patient information, if we cannot interpret the observation result due to the
     * lack of annotation information, we will keep a record of the unrecognized coding system.
     * @param code
     */
    public void addUnrecognizedCode(Code code){
        this.unrecognizedCodes.add(code);
    }

    /**
     * When we run the software to parse patient information, if we cannot interpret the observation result due to the
     * lack of annotation information, we will keep a record of the unrecognized codes (includes code id, system etc).
     * @param code a code that the app cannot recognize
     * @return the annotation object
     */
    public Set<Code> getUnrecognizedCodes(Code code) {
        return this.unrecognizedCodes;
    }

    /**
     * The functions provides a way to access all the coding systems used in the annotation.
     * @return a set of code system names
     */
    public Set<String> getCodeSystems(){
        Set<String> codeSystems = new HashSet<>();
        candidateHpoTerms.keySet().forEach(x -> codeSystems.add(x.getSystem()));
        return codeSystems;
    }

    /**
     *
     * @return
     */
    //@Override
    public Set<Code> getCodes(){
        return candidateHpoTerms.keySet();
    }


    /**
     * Get the corresponding Hpo term for a coded value
     * @param code a code in a coding system. Usually, it is the internal code; for Ord, Nom, or Nar, it can be codes of
     *             an external coding system
     * @return the hpo term wrapped in the HpoTermId4LoincTest class
     */
    public HpoTermId4LoincTest loincInterpretationToHPO(Code code) {
        return candidateHpoTerms.get(code);
    }




    public HashMap<Code, HpoTermId4LoincTest> getCandidateHpoTerms() {
        return new HashMap<>(candidateHpoTerms);
    }



    @Override
    /**
     * The default toString method will serialize all the annotations to a string, including basic and advanced annotations.
     */
    public String toString(){

        StringBuilder stringBuilder = new StringBuilder();
        candidateHpoTerms.forEach((code, hpoTermId4LoincTest) -> {
            stringBuilder.append(this.loincId);
            stringBuilder.append("\t" + this.loincScale.toString());
            stringBuilder.append("\t" + code.getSystem());
            stringBuilder.append("\t" + code.getCode());
            stringBuilder.append("\t" + hpoTermId4LoincTest.getId().getIdWithPrefix());
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

    public String getBasicAnnotationsString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.loincId);
        stringBuilder.append("\t" + this.loincScale.toString());
        stringBuilder.append("\t" + (low == null ? MISSINGVALUE : low.getId().getIdWithPrefix()));
        stringBuilder.append("\t" + (intermediate == null ? MISSINGVALUE : intermediate.getId().getIdWithPrefix()));
        stringBuilder.append("\t" + (high == null ? MISSINGVALUE : high.getId().getIdWithPrefix()));
        stringBuilder.append("\t" + intermediateNegated);
        stringBuilder.append("\t" + (this.note == null ? MISSINGVALUE : this.note));
        stringBuilder.append("\t" + this.flag);
        stringBuilder.append("\t" + String.format("%.1f", this.version));
        stringBuilder.append("\t" + (this.createdOn == null ? MISSINGVALUE : this.createdOn));
        stringBuilder.append("\t" + (this.createdBy == null ? MISSINGVALUE : this.createdBy));
        stringBuilder.append("\t" + (this.lastEditedOn == null ? MISSINGVALUE: this.lastEditedOn));
        stringBuilder.append("\t" + (this.lastEditedBy == null ? MISSINGVALUE : this.lastEditedBy));
        //stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    public String getAdvancedAnnotationsString() {

        StringBuilder stringBuilder = new StringBuilder();
        if (this.advancedAnnotationTerms == null) {
            return null;
        }
        advancedAnnotationTerms.forEach((code, hpoTermId4LoincTest) -> {
            stringBuilder.append(this.loincId);
            stringBuilder.append("\t" + this.loincScale.toString());
            stringBuilder.append("\t" + code.getSystem());
            stringBuilder.append("\t" + code.getCode());
            stringBuilder.append("\t" + hpoTermId4LoincTest.getId().getIdWithPrefix());
            stringBuilder.append("\t" + hpoTermId4LoincTest.isNegated());
            stringBuilder.append("\t" + (this.note == null ? MISSINGVALUE : this.note));
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
        private HpoTerm low = null;
        private HpoTerm intermediate = null;
        private boolean intermediateNegated = false;
        private HpoTerm high = null;
        private Map<Code, HpoTermId4LoincTest> advancedAnnotationTerms = new HashMap<>();
        private LocalDateTime createdOn = null;
        private String createdBy = null;
        private LocalDateTime lastEditedOn = null;
        private String lastEditedBy = null;
        private String note = null;
        private boolean flag = false;
        private double version = 0.0;

        private Map<String, Code> internalCode = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);

        //constructor
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
        public Builder setLowValueHpoTerm(HpoTerm low) {

            this.low = low;
            if (low == null) {
                return this;
            }
            advancedAnnotationTerms.put(internalCode.get("L"),
                        new HpoTermId4LoincTest(low, false));
            return this;

        }

        /**
         * Set the HPO term when the measured value is intermediate (typically "normal")
         * @param intermediate
         */
        public Builder setIntermediateValueHpoTerm(HpoTerm intermediate) {

            this.intermediate = intermediate;
            if (intermediate == null) {
                return this;
            }
            //use the default value for negated, which will be overwritten when user provides real value
            advancedAnnotationTerms.put(internalCode.get("N"),
                    new HpoTermId4LoincTest(intermediate, true));
            advancedAnnotationTerms.put(internalCode.get("A"),
                    new HpoTermId4LoincTest(intermediate, false));
            return this;

        }

        public Builder setIntermediateValueHpoTerm(HpoTerm intermediate, boolean isNegated) {

            this.intermediate = intermediate;
            if (intermediate == null) {
                return this;
            }
            //use the default value for negated, which will be overwritten when user provides real value
            advancedAnnotationTerms.put(internalCode.get("N"),
                    new HpoTermId4LoincTest(intermediate, isNegated));
            advancedAnnotationTerms.put(internalCode.get("A"),
                    new HpoTermId4LoincTest(intermediate, !isNegated));
            return this;

        }

        /**
         * Specify whether the HPO term for the intermediate value should be negated. Typically the value should be true.
         * @param negated
         */
        public Builder setIntermediateNegated(boolean negated) {

            this.intermediateNegated = negated;
            if (intermediate != null) {
                advancedAnnotationTerms.put(internalCode.get("N"),
                        new HpoTermId4LoincTest(intermediate, intermediateNegated));
            }
            return this;

        }



        /**
         * Specify the HPO term when the measured value is high.
         * @param high
         */
        public Builder setHighValueHpoTerm(HpoTerm high) {

            this.high = high;
            if (high == null) {
                return this;
            }
            advancedAnnotationTerms.put(internalCode.get("H"),
                    new HpoTermId4LoincTest(high, false));
            return this;

        }

        public Builder setPosValueHpoTerm(HpoTerm pos) {

            if (pos == null) {
                return this;
            }

            advancedAnnotationTerms.put(internalCode.get("P"),
                    new HpoTermId4LoincTest(pos, false));
            return this;
        }

        public Builder setNegValueHpoTerm(HpoTerm neg, boolean inverse) {

            if (neg == null) {
                return this;
            }

            advancedAnnotationTerms.put(internalCode.get("NP"),
                    new HpoTermId4LoincTest(neg, inverse));
            return this;
        }

        /**
         * Add an annotation in the advanced mode.
         * @param code
         * @param annotation
         * @return
         */
        public Builder addAdvancedAnnotation(Code code, HpoTermId4LoincTest annotation) {

            this.advancedAnnotationTerms.put(code, annotation);
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

        public UniversalLoinc2HPOAnnotation build() {

            return new UniversalLoinc2HPOAnnotation(loincId,
                    loincScale,
                    low,
                    intermediate,
                    intermediateNegated,
                    high,
                    advancedAnnotationTerms,
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

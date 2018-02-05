package org.monarchinitiative.loinc2hpo.loinc;

import com.github.phenomics.ontolib.ontology.data.TermId;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class UniversalLoinc2HPOAnnotation extends Loinc2HPOAnnotation {

    //the keys are internal codes; each one should correspond to one HpoTerm4LoincTest
    //alternatively, the codes can be external codes, if it is for a Ord or Nom loinc test
    //access the codes from the CodeSystemConvertor.getCodeContainer
    private HashMap<Code, HpoTermId4LoincTest> candidateHpoTerms = new HashMap<>();
    private String note;
    private boolean flag;

    private Set<String> codeSystems; //what code systems are used for annotation
    private Set<Code> unrecognizedCodes; //keep a record if a code is not annotated but used in real-world observation

    public UniversalLoinc2HPOAnnotation(){ }

    public UniversalLoinc2HPOAnnotation(LoincId lid, LoincScale lsc){

        super(lid, lsc);

    }

    public UniversalLoinc2HPOAnnotation addAnnotation(Code code, HpoTermId4LoincTest hpoTermId4LoincTest) {
        this.candidateHpoTerms.put(code, hpoTermId4LoincTest);
        return this;
    }

    public UniversalLoinc2HPOAnnotation addAnnotation(Map<Code, HpoTermId4LoincTest> annotation){
        this.candidateHpoTerms.putAll(annotation);
        return this;
    }

    public UniversalLoinc2HPOAnnotation setNote(String note){
        this.note = note;
        return this;
    }

    public UniversalLoinc2HPOAnnotation setFlag(boolean flag) {
        this.flag = flag;
        return this;
    }

    public void addUnrecognizedCode(Code code){
        this.unrecognizedCodes.add(code);
    }
    public Set<Code> getUnrecognizedCodes(Code code) {
        return this.unrecognizedCodes;
    }

    public Set<String> getCodeSystems(){
        Set<String> codeSystems = new HashSet<>();
        candidateHpoTerms.keySet().forEach(x -> codeSystems.add(x.getSystem()));
        return codeSystems;
    }


    @Override
    public  String getNote(){
        return this.note;
    }
    @Override
    public boolean getFlag(){
        return this.flag;
    }

    @Override
    /**
     * Get the hpo term for a coded value
     */
    public HpoTermId4LoincTest loincInterpretationToHPO(Code code) {
        return candidateHpoTerms.get(code);
    }

    private TermId getHpoTermIdForInternalCode(String internalCode){
        Code code = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM).get("L");
        return candidateHpoTerms.get(code).getId();
    }
    @Override
    @Deprecated
    public  TermId getBelowNormalHpoTermId(){
        return getHpoTermIdForInternalCode("L");
    }
    @Override
    @Deprecated
    public  TermId getNotAbnormalHpoTermName(){
        return getHpoTermIdForInternalCode("N");
    }
    @Override
    @Deprecated
    public TermId getAbnormalHpoTermName() {
        return getHpoTermIdForInternalCode("A");
    }
    @Override
    @Deprecated
    public  TermId getAboveNormalHpoTermName(){

        return getHpoTermIdForInternalCode("H");
    }
    @Override
    @Deprecated
    public HpoTermId4LoincTest loincInterpretationToHpo(ObservationResultInInternalCode obs){
        return null;
    }
}

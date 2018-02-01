package org.monarchinitiative.loinc2hpo.loinc;

import com.github.phenomics.ontolib.ontology.data.TermId;
import org.hl7.fhir.dstu3.model.Coding;

import java.util.HashMap;
import java.util.Map;


public class UniversalLoinc2HPOAnnotation extends Loinc2HPOAnnotation {

    private HashMap<Coding, HpoTermId4LoincTest> candidateHpoTerms = new HashMap<>();


    public UniversalLoinc2HPOAnnotation(){
        super();
    }

    public UniversalLoinc2HPOAnnotation(LoincId lid, LoincScale lsc){

        super(lid, lsc);

    }
    
    public UniversalLoinc2HPOAnnotation addAnnotation(Coding coding, HpoTermId4LoincTest hpoTermId4LoincTest) {
        this.candidateHpoTerms.put(coding, hpoTermId4LoincTest);
        return this;
    }

    public UniversalLoinc2HPOAnnotation addAnnotation(Map<Coding, HpoTermId4LoincTest> annotation){
        this.candidateHpoTerms.putAll(annotation);
        return this;
    }



    @Override
    public  String getNote(){
        return null;
    }
    @Override
    public boolean getFlag(){
        return false;
    }
    @Override
    public  TermId getBelowNormalHpoTermId(){
        return null;
    }
    @Override
    public  TermId getNotAbnormalHpoTermName(){
        return null;
    }
    @Override
    public  TermId getAboveNormalHpoTermName(){
        return null;
    }
    @Override
    public  TermId getCorrespondingHpoTermName(){
        return null;
    }
    @Override
    public HpoTermId4LoincTest loincInterpretationToHpo(ObservationResultInInternalCode obs){
        return null;
    }

}

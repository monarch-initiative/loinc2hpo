package org.monarchinitiative.loinc2hpo.codesystems;

import org.hl7.fhir.dstu3.model.Coding;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public abstract class CodeSystem {

    private String system;
    private HashMap<String, Coding> codes;
    private CodeContainer codeContainer;

    public abstract String getSystem();

    public abstract Map<String, Coding> getCodes();
    protected abstract void init();

    public void addToCodeContainer(CodeContainer codeContainer){
        //codeContainer.add(this);
    }



}

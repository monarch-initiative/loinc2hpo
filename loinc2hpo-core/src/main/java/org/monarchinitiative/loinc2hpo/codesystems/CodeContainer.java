package org.monarchinitiative.loinc2hpo.codesystems;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a class (singleton) to contain the code systems that are used in the app.
 * e.g. "http://jax.org/loinc2hpo" is the system for internal code system,
 * and we define codes "L", "H" ... for internal use. To access "H" in the system,
 * call getCodeSystemMap.get("http://jax.org/loinc2hpo").get("H")
 * When a new code system is defined, the "add()" function is called.
 */

public class CodeContainer {

    //collect codes list and access by the system: system -> codes in the system
    private Map<String, Map<String, Code>> codelists = new HashMap<>();

    private static CodeContainer instance = null;
    private CodeContainer(){

    }
    public static CodeContainer getInstance(){
        if (instance==null){
            instance = new CodeContainer();
        }
        return instance;
    }

    public void add(Code code){

        if (!codelists.containsKey(code.getSystem())){
            Map<String, Code> newCodeSystem = new HashMap<>();
            codelists.put(code.getSystem(), newCodeSystem);
        }

        codelists.get(code.getSystem()).put(code.getCode(), code);

    }
    public Map<String, Map<String, Code>> getCodeSystemMap(){
        return codelists;
    }

}

package org.monarchinitiative.loinc2hpo.codesystems;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a class to contain the code systems that are used in the app.
 * A map is used to store the code systems (system : codeSystem),
 * e.g. "http://jax.org/loinc2hpo" is the system for internal code system,
 * and we define codes "L", "H" ... for internal use.
 * When a new code system is defined, the "add()" function is called.
 */
@Deprecated
public class CodeContainer {

    private static Map<String, CodeSystem> codeSystemMap;
    private static CodeContainer instance = null;
    private void CodeContainer(){
        codeSystemMap = new HashMap<>();
        this.add(new CodeSystemInternal());
    }
    public static CodeContainer getInstance(){
        if (instance==null){
            instance = new CodeContainer();
        }
        return instance;
    }

    public static void add(CodeSystem codeSystem){
        codeSystemMap.put(codeSystem.getSystem(), codeSystem);
    }
    public static Map<String, CodeSystem> getCodeSystemMap(){
        return codeSystemMap;
    }

}

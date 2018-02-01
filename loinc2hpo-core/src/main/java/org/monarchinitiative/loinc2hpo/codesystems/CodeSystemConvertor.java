package org.monarchinitiative.loinc2hpo.codesystems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.codesystems.V3ObservationInterpretation;

import java.util.HashMap;
import java.util.Map;

public class CodeSystemConvertor {

    private static Map<V3ObservationInterpretation, Loinc2HPOCodedValue> v3toInternalCodeMap;

    private static final Logger logger = LogManager.getLogger();

    static {
        init();
    }
    static void init(){
        initV3toInternalCodeMap();
        //create many other maps
    }
    static void initV3toInternalCodeMap(){
        v3toInternalCodeMap = new HashMap<>();
        String[] v3Codes = new String[] {
                "<",
                ">",
                "H",
                "HH",
                "I",
                "N",
                "L",
                "LL",
                "POS",
                "NEG",
                "W"
        };
        String[] internalCodes = new String[]{
                "L",
                "H",
                "H",
                "H",
                "N",
                "N",
                "L",
                "L",
                "P",
                "NP",
                "U"
        };

        for (int i = 0; i < v3Codes.length; i++) {
            try{
                v3toInternalCodeMap.put(V3ObservationInterpretation.fromCode(v3Codes[i]),
                        Loinc2HPOCodedValue.fromCode(internalCodes[i]));
            } catch (Exception e) {
                logger.error("some code are not recognized.");
            }
        }

    }


    private Map<V3ObservationInterpretation, Loinc2HPOCodedValue> v3toInternal;


    public static Loinc2HPOCodedValue convertToInternalCode(V3ObservationInterpretation other){
        return v3toInternalCodeMap.get(other);
    }

    /**
     * just for unit test
     * @return
     */
    public static Map<V3ObservationInterpretation, Loinc2HPOCodedValue> getV3toInternalCodeMap(){
        return v3toInternalCodeMap;
    }
}

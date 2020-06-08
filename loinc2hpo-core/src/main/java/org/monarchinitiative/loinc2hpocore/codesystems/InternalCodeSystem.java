package org.monarchinitiative.loinc2hpocore.codesystems;

import java.util.HashMap;
import java.util.Map;

public class InternalCodeSystem {
    public static final String SYSTEMNAME = "FHIR";
    private static Map<InternalCode, Code> internalCodeMap;

    static {
        internalCodeMap = new HashMap<>();
        internalCodeMap.put(InternalCode.A, new Code(SYSTEMNAME, "A", "abnormal"));
        internalCodeMap.put(InternalCode.L, new Code(SYSTEMNAME, "L", "low"));
        internalCodeMap.put(InternalCode.N, new Code(SYSTEMNAME, "N", "normal"));
        internalCodeMap.put(InternalCode.H, new Code(SYSTEMNAME, "H", "high"));
        internalCodeMap.put(InternalCode.U, new Code(SYSTEMNAME, "U", "unknown"));
        internalCodeMap.put(InternalCode.NEG, new Code(SYSTEMNAME, "NEG",
                "absent"));
        internalCodeMap.put(InternalCode.POS, new Code(SYSTEMNAME, "POS",
                "present"));
    }

    public static Code getCode(InternalCode internalCode){
        return internalCodeMap.get(internalCode);
    }

}

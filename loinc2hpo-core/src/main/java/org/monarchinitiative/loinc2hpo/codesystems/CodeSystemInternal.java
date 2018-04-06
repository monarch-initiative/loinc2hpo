package org.monarchinitiative.loinc2hpo.codesystems;

import org.hl7.fhir.dstu3.model.Coding;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class CodeSystemInternal extends CodeSystem {

    private final String INTERNALSYSTEM = "FHIR";;
    private HashMap<String, Coding> codes;

    public CodeSystemInternal(){
        init();
    }

    @Override
    protected void init(){
        final Coding BELOWNORMAL = new Coding(INTERNALSYSTEM, "L", "below normal range");
        final Coding NORMAL = new Coding(INTERNALSYSTEM, "N", "within normal range");
        final Coding ABOVENORMAL = new Coding(INTERNALSYSTEM, "H", "above normal range");
        final Coding ABSENCE = new Coding(INTERNALSYSTEM, "NP", "not present");
        final Coding PRESENCE = new Coding(INTERNALSYSTEM, "P", "present");
        final Coding UNKNOWN = new Coding(INTERNALSYSTEM, "unknown", "unknown");

        codes.put(BELOWNORMAL.getCode(), BELOWNORMAL);
        codes.put(NORMAL.getCode(), NORMAL);
        codes.put(ABOVENORMAL.getCode(), ABOVENORMAL);
        codes.put(ABSENCE.getCode(), ABSENCE);
        codes.put(PRESENCE.getCode(), PRESENCE);
        codes.put(UNKNOWN.getCode(), UNKNOWN);

    }

    @Override
    public String getSystem(){
        return this.INTERNALSYSTEM;
    }

    @Override
    public Map<String, Coding> getCodes(){
        return this.codes;
    }


}

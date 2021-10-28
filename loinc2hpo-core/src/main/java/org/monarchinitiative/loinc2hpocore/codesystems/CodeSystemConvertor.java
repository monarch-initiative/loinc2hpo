package org.monarchinitiative.loinc2hpocore.codesystems;

import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CodeSystemConvertor {
    private static final Logger logger = LoggerFactory.getLogger(CodeSystemConvertor.class);

    private final Map<Code, Code> codeConversionmap = new HashMap<>();

    public CodeSystemConvertor(){
        initV2toInternalCodeMap();
    }

    //add default conversion map from FHIR V2 codeset to internal codes
    private void initV2toInternalCodeMap(){
        final String v2System = "http://hl7.org/fhir/v2/0078";
        InputStream mappath = CodeSystemConvertor.class.getClassLoader().getResourceAsStream("external2internalCodeMap/HL7_v2_table0078_to_internal.tsv");
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mappath, StandardCharsets.UTF_8))){
            //ignore header
            String line = bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                String[] elements = line.split("\\t");
                if (elements.length == 3) {
                    Code v2Code = new Code(v2System, elements[0], null);
                    Code internalCode =
                            InternalCodeSystem.getCode(InternalCode.fromCode(elements[2]));
                    this.codeConversionmap.put(v2Code, internalCode);
                } else {
                    logger.error("The line does not have 3 tab-separated elements: " + line);
                }
            }
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }

    /**
     * Add additional code conversion maps to our internal code
     * @param newMap
     */
    public void addCodeConversionMap(Map<Code, Code> newMap){
        this.codeConversionmap.putAll(newMap);
    }


    public Code convertToInternalCode(Code code) {
        System.out.println(code);
        if (!this.codeConversionmap.containsKey(code)) {
            throw Loinc2HpoRuntimeException.internalCodeNotFound(code.getSystem() + ":" + code.getCode());
        }
        return this.codeConversionmap.get(code);

    }

    public Map<Code, Code> getCodeConversionmap(){
        return new HashMap<>(this.codeConversionmap);
    }

}

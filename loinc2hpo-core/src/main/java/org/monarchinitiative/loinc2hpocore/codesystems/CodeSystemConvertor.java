package org.monarchinitiative.loinc2hpocore.codesystems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpocore.exception.InternalCodeNotFoundException;
import org.monarchinitiative.loinc2hpocore.exception.UnrecognizedCodeException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class CodeSystemConvertor {
    private static final Logger logger = LogManager.getLogger();

    private Map<Code, Code> codeConversionmap = new HashMap<>();

    public CodeSystemConvertor(){
        initV2toInternalCodeMap();
    }

    private void initV2toInternalCodeMap(){
        final String v2System = "http://hl7.org/fhir/v2/0078";
        final String internalSystem = InternalCodeSystem.SYSTEMNAME;
        InputStream mappath = CodeSystemConvertor.class.getClassLoader().getResourceAsStream("external2internalCodeMap/HL7_v2_table0078_to_internal.tsv");
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mappath, Charset.forName("UTF-8")))){
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

        } catch (UnrecognizedCodeException e) {
            e.printStackTrace();
            throw new RuntimeException("unrecognized code encountered");
        }
    }


    public void addCodeConversionMap(Map<Code, Code> newMap){
        this.codeConversionmap.putAll(newMap);
    }


    public Code convertToInternalCode(Code code) throws InternalCodeNotFoundException {

        if (!this.codeConversionmap.containsKey(code)) {
            throw new InternalCodeNotFoundException("Could not find an internal code that match to: " + code.getSystem() + " " + code.getCode());
        }
        return this.codeConversionmap.get(code);

    }

    public Map<Code, Code> getCodeConversionmap(){
        return new HashMap<>(this.codeConversionmap);
    }

}

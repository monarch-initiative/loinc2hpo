package org.monarchinitiative.loinc2hpo.codesystems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.exception.InternalCodeNotFoundException;
import org.monarchinitiative.loinc2hpo.exception.LoincCodeNotFoundException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class CodeSystemConvertor {
    private static final Logger logger = LogManager.getLogger();

    private static CodeContainer codeContainer;
    private static Map<Code, Code> codeConversionmap;

    //private static Map<Coding, Coding> codeMapCollection;  //TODO: how to use fhir coding correctly?

    static {
        codeContainer = CodeContainer.getInstance();
        codeConversionmap = new HashMap<>();
        init();
    }
    static void init(){
        addRelevantCodeSystems();
        initV2toInternalCodeMap();
        //create many other maps
    }
    private static void addRelevantCodeSystems(){

        for (Loinc2HPOCodedValue code : Loinc2HPOCodedValue.class.getEnumConstants()){
            Code newCode = Code.getNewCode()
                    .setSystem(code.getSystem())
                    .setCode(code.toCode())
                    .setDisplay(code.getDisplay())
                    .setDefinition(code.getDefinition());
            logger.debug(newCode.toString());
            codeContainer.add(newCode);
        }

        //add HL7 V2 0078 interpretation values
        String path = CodeSystemConvertor.class.getClassLoader().getResource("CodeSystems/HL7_V2_table0078.tsv").getPath();
        System.out.println(path);
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), Charset.forName("UTF-8")))) {
            String line = bufferedReader.readLine();
            System.out.println(line);
            if (line == null || line.trim().isEmpty()) {
                logger.error("File is empty or first line is empty. First line should be code system");
                return;
            }
            if (line.split("\\t").length != 2) {
                logger.error("First line is not formatted correctly");
                return;
            }
            String system = line.split("\\t")[1];
            while (line != null) {

                if (!line.startsWith("Code")) {
                    String[] elements = line.split("\\t");
                    if (elements.length == 6 || elements.length == 5) { //last line only has five elements
                        String code = elements[0];
                        String display = elements[1];
                        String definition = elements[4];
                        Code newCode = Code.getNewCode().setSystem(system).setCode(code).setDisplay(display).setDefinition(definition);
                        codeContainer.add(newCode);
                    } else {

                    }
                }
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //add HL V3 interpretation values
    }

    static void initV2toInternalCodeMap(){
        final String v2System = "http://hl7.org/fhir/v2/0078";
        String[] v2Codes = new String[] {
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
        final String internalSystem = Loinc2HPOCodedValue.CODESYSTEM;
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

        for (int i = 0; i < v2Codes.length; i++) {
            Code v2Code = codeContainer.getCodeSystemMap().get(v2System).get(v2Codes[i]);
            Code internalCode = codeContainer.getCodeSystemMap().get(internalSystem).get(internalCodes[i]);
            if (v2Code != null && internalCode != null){
                codeConversionmap.put(v2Code, internalCode);
            }
        }

    }

    public static Code convertToInternalCode(Code code) throws InternalCodeNotFoundException {

        if (!codeConversionmap.containsKey(code)) {
            throw new InternalCodeNotFoundException("Could not find an internal code that match to: " + code.getSystem() + " " + code.getCode());
        }
        return codeConversionmap.get(code);

    }
    public static CodeContainer getCodeContainer(){
        return codeContainer;
    }

    /**
     * just for unit test
     * @return
     */
    public static Map<Code, Code> getCodeConversionMap(){
        return codeConversionmap;
    }

}

package org.monarchinitiative.loinc2hpocore.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;


/**
 * Parse the LOINC Core Table file
 *
 * LOINC_NUM
 * The fields are
 * <ol>
 *     <li>LOINC_NUM, e.g., "2823-3"</li>
 *     <li>COMPONENT, e.g., "Potassium"</li>
 *     <li>PROPERTY, e.g., "SCnc"</li>
 *     <li>TIME_ASPCT, e.g., "Pt"</li>
 *     <li>CODESYSTEM, e.g., "Ser/Plas"</li>
 *     <li>SCALE_TYP, e.g.,"Qn"</li>
 *     <li>METHOD_TYP, e.g., blank</li>
 *     <li>CLASS, e.g., "CHEM"</li>
 *     <li>CLASSTYPE, e.g., 1</li>
 *     <li>LONG_COMMON_NAME, e.g., "Potassium [Moles/volume] in Serum or Plasma"</li>
 *     <li>SHORTNAME, e.g., "Potassium SerPl-sCnc"</li>
 *     <li>EXTERNAL_COPYRIGHT_NOTICE, e.g., </li>
 *     <li>STATUS, e.g., "ACTIVE"</li>
 *     <li>VersionFirstReleased, e.g.,"1.0"</li>
 *     <li>VersionLastChanged, e.g., "2.34"</li>
 * </ol>
 *
q
 */
public class LoincImporter {
    private static final Logger logger = LogManager.getLogger();

    /**
     * A utility to import a line of the LOINC code csv file that represents a sinble LOINC entry into the file
     * resources/loinctest2hpo. For now, we will import one line at a time, work on the Hpo mapping, and test.
     *
     * @param path path to LaincTableCore
     * @param code name of a LOINC code we want to import, e.g., 10055-2
     */
    public LoincImporter(String path, String code) {
        getLOINCline(path,code);
    }

    /**
     * Splits strings line "2823-3","Potassium","SCnc", on the comma. THe returned list also removes the quotation marks
     * @param s string with a comma separated list of quoted strings
     * @return List with individual entries with quotation marks removed.
     */
    public static List<String> splitCSVquoted(String s)
    {
        List<String> words = new ArrayList<>();
        boolean notInsideComma = true;
        int start =0, end=0;
        for(int i=0; i<s.length()-1; i++)
        {
            if(s.charAt(i)==',' && notInsideComma)
            {
                words.add(s.substring(start,i).replaceAll("\"",""));
                start = i+1;
            }
            else if(s.charAt(i)=='"')
                notInsideComma=!notInsideComma;
        }
        words.add(s.substring(start).replaceAll("\"",""));
        return words;
    }


    private void getLOINCline(String path, String code) {
        logger.trace(String.format("Searching for loinc data about code \"%s\"",code));
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.contains(code)) {
                    List<String> splitted = splitCSVquoted(line);
                    if (splitted.size()>0 && splitted.get(0).equals(code)) {
                        System.out.println(line);
                        for (String s : splitted) {
                            System.out.println(s);

                        }
                    }
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void appendCode(String path, String code) {
        try {
            Files.write(Paths.get(path), "the text".getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }


}

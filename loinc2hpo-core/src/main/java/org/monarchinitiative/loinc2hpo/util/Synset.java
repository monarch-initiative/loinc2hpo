package org.monarchinitiative.loinc2hpo.util;

import java.util.*;

/**
 * This class is responsible for returning synsets.
 * @TODO: either implement our own synsets or find an api that can do this
 * Reference: http://wordnetweb.princeton.edu/perl/webwn?s=red+blood+cell&sub=Search+WordNet&o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&h=0000
 */
public class Synset {

    private static String[][] synonyms = new String[][] {
            {"blood", "plasma", "serum"},
            {"urine", "urinary"},
            {"kidney", "renal"},
            {"RBC", "red blood cell", "erythrocyte"},
            {"heart", "cardia*"}
    };
    //TODO: more

    public static List<String> getSynset(String key) {
        for (int i = 0; i < synonyms.length; i++) {
            for (int j = 0; j < synonyms[i].length; j++) {
                if (key.equals(synonyms[i][j])) {
                    return Arrays.asList(synonyms[i]);
                }
            }
        }
        List<String> toReturn = new ArrayList<>();
        toReturn.add(key);
        return toReturn;
    }
}

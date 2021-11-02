package org.monarchinitiative.loinc2hpocore.loinc;

import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * A class to represent a loinc code. The class is created by
 * LoincLongNameParser. It has fields for parameter, tissue, assay method and
 * assayType.
 * e.g. "Erythrocytes [#/volume] in Blood by Automated Count" is the long
 * common name for a loinc assay. We represent this loinc assay with this
 * class.
 * parameter:   Erythrocytes
 * tissue:      Blood
 * assayMethod: Automated Count
 * assayType:   #/volume
 * Note: do not take "tissue" too literal. For some loinc names, what binds
 * to "tissue" might not be a biological tissue.
 * The class also has two public methods keysInLoincParameter() and
 * keysInLoincTissue() besides standard getters. Those methods will return
 * valid keys in loinc parameter and tissue for Sparql query
 */
public class LoincLongName {

    private final String parameter;
    private final String tissue;
    private final String assayMethod;
    private final String assayType;
    private final String name;
    /** List of stop words and general words that we filter out of LOINC labels prior to text mining. */
    private static final Set<String> invalid_words =
            Set.of("mean", "in", "of", "identified", "cell", "conjugated", "other", "virus",
                    "normal", "on", "total");


    public LoincLongName(String name, String parameter, String tissue, String method, String type) {
        this.name = name;
        this.parameter = parameter;
        this.tissue = tissue;
        this.assayMethod = method;
        this.assayType = type;
    }


    /**
     * Extract and return the parameter from a Loinc long common name.
     * e.g. "Platelet mean volume [Entitic volume] in Blood by Automated count"
     * The method will return "Platelet mean volume".
     * @return parameter measured from a Loinc long common name; "" if such information is not identified.
     */
    public String getLoincParameter(){
        return this.parameter;
    }

    /**
     * Extract and return the tissue from a Loinc long common name.
     * e.g. "Platelet mean volume [Entitic volume] in Blood by Automated count"
     * The method will return "Blood".
     * @return tissued measured from a Loinc long common name; "" if such information is not identified.
     */
    public String getLoincTissue(){
        return this.tissue;
    }

    /**
     * Extract and return the method from a Loinc long common name.
     * e.g. "Platelet mean volume [Entitic volume] in Blood by Automated count"
     * The method will return "Automated count".
     * @return method used from a Loinc long common name; "" if such information is not identified.
     */
    public String getLoincMethod(){
        return this.assayMethod;
    }

    /**
     * Extract and return the type of a Loinc assay from a Loinc long common name.
     * e.g. "Platelet mean volume [Entitic volume] in Blood by Automated count"
     * The method will return "Entitic volume" (Strings in brackets).
     * @return type used from a Loinc long common name; "" if such information is not identified.
     */
    public String getLoincType(){
        return this.assayType;
    }

    /**
     * Check words in "parameter" and return a list of valid words as keys
     * for Sparql query. Non-valid words include stop words in English (e.g.
     * "in", "on", "of") and words that are too general (e.g. "cell", "mean")
     * or integers.
     * @return a list of valid words
     */
    public Queue<String> keysInLoincParameter() {
        Queue<String> keys = new LinkedList<>();
        String[] words = this.parameter.split("\\W");
        for (String word : words) {
            if(validKey(word.toLowerCase())) {
                keys.add(trimS(word));
            }
        }
        return keys;
    }


    /**
     * Check words in "tissue" and return a list of valid words as keys
     * for Sparql query. Non-valid words include stop words in English (e.g.
     * "in", "on", "of") and words that are too general (e.g. "cell", "mean")
     * or integers.
     * @return a list of valid words
     */
    public Queue<String> keysInLoincTissue() {
        Queue<String> keys = new LinkedList<>();
        String[] words = this.tissue.split(" or ");
        for (String word : words) {
            if(validKey(word.toLowerCase())) {
                keys.add(trimS(word));
            }
        }
        return keys;
    }

    /**
     * Remove trailing 's' from a world and replace it with a '*'
     */
    private static String trimS(String s) {
        String newString;
        if (s.endsWith("s") && s.length() > 2) {
            newString = s.substring(0, s.length() - 1) + "*";
        } else {
            newString = s;
        }
        return newString;
    }

    /**
     * A helper method to check whether a word is valid or not, i.e., whether a word should be used in building a query
     * Do not use numbers, empty/null words, one-letter words, or words in {@link #invalid_words}.
     * @param word word to be tested
     * @return true if the word can be used to build a query.
     */
    private static boolean validKey(String word) {
        final Pattern pattern = Pattern.compile("[0-9]+");
        final Predicate<String> integerPredicate = pattern.asPredicate();
        if (word == null || word.isEmpty()) {
            return false;
        } else if (word.length() == 1) {
            return false;
        } else if (invalid_words.contains(word.toLowerCase())) {
            return false;
        } else {
            return !integerPredicate.test(word);
        }
    }

    /**
     * A static method to parse a loinc long common name and return a
     * LoincLongNameComponents
     * @param loincLongName long name field of the LoincTableCore.csv file
     * @return a LoincLongNameComponents
     */
    public static LoincLongName of(String loincLongName) {

        String parameter = "";
        String tissue = "";
        String assayMethod = "";
        String assayType = "";


        if (loincLongName == null) {
            throw new Loinc2HpoRuntimeException("Null pointer LOINC Long Name");
        }
        //split loinc long common name by "by"
        //example: "Erythrocyte distribution width [Ratio] by Automated count"
        //example: "Platelets [#/volume] in Blood by Automated count"
        String[] elems1 = loincLongName.split(" by ");
        if (elems1.length == 2) {
            assayMethod = elems1[1]; //this is the method for the Loinc assay
        }
        String[] elems2 = elems1[0].split(" in ");
        if (elems2.length == 1) {//if no "in", then it might be "of". example: Hematocrit [Volume Fraction] of Blood
            elems2 = elems2[0].split(" of ");
        }
        if (elems2.length == 1) {//if not "in", then try "on". example: little i Ag [Presence] on Red Blood Cells from donor
            elems2 = elems2[0].split(" on ");
        }
        if (elems2.length == 1) {
            String[] elems3 = elems2[0].split(" \\[");
            if (elems3.length == 1) {
                parameter = elems3[0];
            }
            if (elems3.length == 2) {
                parameter = elems3[0];
                assayType = elems3[1].split("]")[0];
            }
        }
        if (elems2.length == 3) { //e.g. "Cholesterol in HDL [Presence] in Serum or Plasma"
            elems2 = new String[]{elems2[0] + " in " + elems2[1], elems2[2]};
        }
        if (elems2.length == 2) { //"Platelets [#/volume]", "Blood"
            if (elems2[1].split(" from ").length == 2) {
                tissue = elems2[1].split(" from ")[0];
            } else if (elems2[1].split(" --").length == 2) {
                tissue = elems2[1].split(" --")[0];
            } else {
                tissue = elems2[1];
            }

            String[] elems3 = elems2[0].split(" \\[");
            if (elems3.length == 1) {
                parameter = elems3[0];
            }
            if (elems3.length == 2) {
                parameter = elems3[0];
                assayType = elems3[1].split("]")[0];
                if(elems3[1].split("]").length == 2) {
                    parameter = parameter + elems3[1].split("]")[1];
                }
            }
        }
        return new LoincLongName(loincLongName, parameter, tissue, assayMethod, assayType);
    }

    public String getName() {
        return name;
    }
}

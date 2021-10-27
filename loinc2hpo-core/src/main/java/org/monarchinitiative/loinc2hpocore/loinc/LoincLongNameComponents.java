package org.monarchinitiative.loinc2hpocore.loinc;

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
public class LoincLongNameComponents {

    private final String parameter;
    private final String tissue;
    private final String assayMethod;
    private final String assayType;
    /** List of stop words and general words that we filter out of LOINC labels prior to text mining. */
    private static final Set<String> invalid_words =
            Set.of("mean", "in", "of", "identified", "cell", "conjugated", "other", "virus",
                    "normal", "on", "total");


    public LoincLongNameComponents(String parameter, String tissue, String method, String type) {
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
}

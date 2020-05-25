package org.monarchinitiative.loinc2hpocore.util;

import java.util.*;

/**
 * This class is responsible for checking whether a key or a list of keys has
 * synsets. If such synsets exist, return the words in the synsets as a
 * regular expression pattern (as a string)
 *
 * @TODO: either implement our own synsets or find an api that can do this
 * Reference: http://wordnetweb.princeton.edu/perl/webwn?s=red+blood+cell&sub=Search+WordNet&o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&h=0000
 */
public class Synset {

    private HashSet<String> synset = new HashSet<>();
    private static final String[][] synonyms = new String[][] {
            {"blood", "plasma", "serum"},
            {"urine", "urinary"},
            {"kidney", "renal"},
            {"RBC", "red blood cell", "erythrocyte"},
            {"heart", "cardia.*"},
            {"pH", "acid", "alkaline", "base", "acidity", "basic"}
    };
    //TODO: add more synsets


    /**
     * Search for the synset of a key
     * @param key
     * @return row number of the synset for key; -1 if the key does not have a synset
     */
    public int getRow(String key) {
        for (int i = 0; i < synonyms.length; i++){
            for (int j = 0; j < synonyms[i].length; j++) {
                if (key.toLowerCase().equals(synonyms[i][j].toLowerCase())) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * A helper method for testing only
     * @return
     */
    public Queue<String> getSynset() {
        return new LinkedList<>(this.synset);
    }

    /**
     * Given one key, get its synset. Immediately call function convertToRe to
     * convert the synset into regular expression (strings seperated by "|")
     * @param key
     * @return the instance
     */
    public Synset getSynset(String key) {
        int row_of_key = getRow(key);
        if(row_of_key != -1) {
            this.synset.addAll(Arrays.asList(synonyms[row_of_key]));
        } else {
            this.synset.add(key);
        }
        return this;
    }

    @Deprecated
    /**
     * Given two keys, get their synsets. Immediately call function convertToRe to
     * convert the synset into regular expression (strings seperated by "|")
     * @param key
     * @return the instance
     */
    public Synset getSynset(String key1, String key2){
        int row_of_key1 = getRow(key1);
        int row_of_key2 = getRow(key2);
        if (row_of_key1 != -1 && row_of_key2 != -1) {
            if (row_of_key1 == row_of_key2) {
                this.synset.addAll(Arrays.asList(synonyms[row_of_key1]));
            } else {
                this.synset.addAll(Arrays.asList(synonyms[row_of_key1]));
                this.synset.addAll(Arrays.asList(synonyms[row_of_key2]));
            }
        } else if (row_of_key1 == -1 && row_of_key2 != -1) {
            this.synset.addAll(Arrays.asList(synonyms[row_of_key2]));
            this.synset.add(key1);
        } else if (row_of_key1 != -1 && row_of_key2 == -1) {
            this.synset.addAll(Arrays.asList(synonyms[row_of_key1]));
            this.synset.add(key2);
        } else {
            this.synset.add(key1);
            this.synset.add(key2);
        }
        return this;
    }


    /**
     * Given a list of keys, get their synsets. The method does not require
     * the keys to be synonyms of each other. Immediately call function
     * convertToRe to convert the synset into regular expression (strings
     * seperated by "|")
     * @param keys a list of keys
     * @return the instance
     */
    public Synset getSynset(List<String> keys) {

        for (String key : keys) {
            int row = getRow(key);
            if (row == -1) {
                this.synset.add(key);
            } else {
                this.synset.addAll(Arrays.asList(synonyms[row]));
            }
        }
        return this;
    }


    /**
     * Convert a list of words in synset(s) into a regular expression pattern.
     * e.g. Calling getSynset with keys (serum, brain) will return a set {blood,
     * plasma, serum, brain}. This function converts the set into pattern "blood|
     * plasma/serum/brain".
     * @return a regular expression pattern (as a string)
     */
    public String convertToRe(){

        StringBuilder keylist = new StringBuilder();
        Iterator<String> itr = this.synset.iterator();
        if (itr.hasNext()) {
            keylist.append(itr.next());
        }
        while(itr.hasNext()) {
            keylist.append("|" + itr.next());
        }
        return keylist.toString();
    }
}

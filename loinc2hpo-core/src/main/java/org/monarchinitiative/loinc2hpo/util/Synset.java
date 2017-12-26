package org.monarchinitiative.loinc2hpo.util;

import java.util.*;

/**
 * This class is responsible for returning synsets.
 * @TODO: either implement our own synsets or find an api that can do this
 * Reference: http://wordnetweb.princeton.edu/perl/webwn?s=red+blood+cell&sub=Search+WordNet&o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&h=0000
 */
public class Synset {

    //private String key;
    private HashSet<String> synset = new HashSet<>();

    private static String[][] synonyms = new String[][] {
            {"blood", "plasma", "serum"},
            {"urine", "urinary"},
            {"kidney", "renal"},
            {"RBC", "red blood cell", "erythrocyte"},
            {"heart", "cardia*"}
    };
    //TODO: more


    public int getRow(String key) {
        for (int i = 0; i < synonyms.length; i++){
            for (int j = 0; j < synonyms[i].length; j++) {
                if (key.equals(synonyms[i][j])) {
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

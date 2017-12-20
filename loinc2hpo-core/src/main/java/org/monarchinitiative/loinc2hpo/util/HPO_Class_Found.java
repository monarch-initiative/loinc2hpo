package org.monarchinitiative.loinc2hpo.util;

import org.apache.commons.collections4.trie.PatriciaTrie;

public class HPO_Class_Found implements Comparable {

    private String id;
    private String label;
    private String definition;
    private LoincCodeClass loinc; //We found this HPO class with this loinc query
    private int score;

    public HPO_Class_Found(String id, String label, String definition, LoincCodeClass loinc) {
        this.id = id;
        this.label = label;
        this.definition = definition;
        this.score = 0;
        this.loinc = loinc;
        calculatePriority();
    }
    private void calculatePriority() {
        //build a dictionary from the HPO class label and definition
        //count keyword (loinc parameter and tissue) matching to this dictionary
        //give scores
        PatriciaTrie<String> pt = new PatriciaTrie<>();
        for (String word : this.label.split(" ")) {
            if (word != null && !word.equals("")) {
                try {
                    Integer.parseInt(word);
                } catch (NumberFormatException e) {
                    //pt.put(word, true);
                }
            }

        }


    }
    public void setID(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public String getDefinition() {
        return this.definition;
    }

    public LoincCodeClass getLoinc() {
        return loinc;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof HPO_Class_Found) {
            HPO_Class_Found other = (HPO_Class_Found) o;
            return this.score - other.score;
        } else {
            throw new IllegalArgumentException();
        }
    }
}

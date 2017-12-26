package org.monarchinitiative.loinc2hpo.util;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.List;
import java.util.Queue;

public class HPO_Class_Found implements Comparable {

    private String id;
    private String label;
    private String definition;
    private LoincCodeClass loinc; //We found this HPO class with this loinc query
    private int score;
    static String modifier = "\"increase*\"|\"decrease*\"|\"elevate*\"|" +
            "\"reduc*\"|\"high*\"|\"low*\"|\"above\"|\"below\"|\"abnormal*\"";

    public HPO_Class_Found(String id, String label, String definition, LoincCodeClass loinc) {
        this.id = id;
        this.label = label;
        this.definition = definition;
        this.loinc = loinc;
        this.score = calculatePriority();
    }
    private int calculatePriority() {
        int matchScore = 0;
        String total = this.label;
        if (this.definition != null)
            total += (" " + this.definition);

        if (total.toLowerCase().matches(modifier)) { //test whether the class has modifier
            matchScore += 50;
        }

        //test key matches
        Queue<String> keysInParameter = this.loinc.keysInLoinParameter();
        for (String key : keysInParameter) {
            if(total.toLowerCase().matches(toPattern(key)) && keysInParameter.size() != 0) {
                matchScore += 30 / keysInParameter.size();
            }
        }

        //test tissue matches
        if (this.loinc.keysInLoincTissue().size() != 0) {
            String keysInTissue = new Synset().getSynset((List<String>)this.loinc.keysInLoincTissue()).convertToRe();
            if (total.toLowerCase().matches(toPattern(keysInTissue))) {
                matchScore += 20;
            }
        }
        return matchScore;
    }
    public String toPattern(String key) {
        return ".*\"" + key.toLowerCase() + "\".*";
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

    public int getScore() { return this.score; }

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

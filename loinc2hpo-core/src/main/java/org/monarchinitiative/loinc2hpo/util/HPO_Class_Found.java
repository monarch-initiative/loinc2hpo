package org.monarchinitiative.loinc2hpo.util;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        this.loinc = loinc;
        this.score = calculatePriority();
    }

    private int calculatePriority() {
        int matchScore = 0;
        String total = this.label;
        if (this.definition != null)
            total += (" " + this.definition);

        Pattern pattern = Pattern.compile(toPattern(SparqlQuery.modifier));
        Matcher matcher = pattern.matcher(total.toLowerCase());
        if (matcher.matches()) { //test whether the class has modifier
            matchScore += 50;
        }

        //test key matches
        Queue<String> keysInParameter = this.loinc.keysInLoinParameter();
        for (String key : keysInParameter) {
            pattern = Pattern.compile(toPattern(key));
            matcher = pattern.matcher(total.toLowerCase());
            if(matcher.matches() && keysInParameter.size() != 0) {
                matchScore += 30 / keysInParameter.size();
            }
        }

        //test tissue matches
        if (this.loinc.keysInLoincTissue().size() != 0) {
            String keysInTissue = new Synset().getSynset(new LinkedList<>(this.loinc.keysInLoincTissue())).convertToRe();
            //String keysInTissue = new Synset().getSynset(new ArrayList<>(this.loinc.keysInLoincTissue())).convertToRe();
            pattern = Pattern.compile(toPattern(keysInTissue));
            matcher = pattern.matcher(total.toLowerCase());
            /**
            System.out.println("how many keys in the loinc class: " + this.loinc.keysInLoincTissue().size());
            System.out.println("\nString for pattern: " + keysInTissue);
            System.out.println(total);
            System.out.println("match? " + matcher.matches());
             **/
            if (matcher.matches()) {
                matchScore += 20;
            }
        }
        return matchScore;
    }
    public String toPattern(String key) {
        return ".*(" + key.toLowerCase() + ").*";
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

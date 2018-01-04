package org.monarchinitiative.loinc2hpo.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents an HPO class returned by a Sparql query. The class
 * keeps a selected information of those HPO classes, id/uri, label and
 * definition. It also keeps record of what loinc code is used for finding
 * the HPO class. The class is comparable by the scores it receives from
 * keyword matching (from the loinc code used for query and the HPO class).
 */
public class HPO_Class_Found implements Comparable {

    private String id; //uri of HPO class
    private String label; //all classes should have a non-null label
    private String definition; //some classes do not have a definition
    private LoincCodeClass loinc; //We found this HPO class with this loinc query
    private int score; //how well the HPO class matches the loinc code (long
                        // common name)

    public HPO_Class_Found(String id, String label, String definition, LoincCodeClass loinc) {
        this.id = id;
        this.label = label;
        this.definition = definition;
        this.loinc = loinc;
        if (loinc != null) {
            this.score = calculatePriority();
        } else {
            this.score = -999;
        }
    }

    /**
     * A helper method to calculate the score. It does so by three steps:
     * 1. if the class have a modifier (increase/decrease...), it receives 50
     * points.
     * 2. it examines the keys in loinc "parameter". A complete match
     * receives 30 points. A partial match receives points based on how many
     * keys are matched.
     * 3. it examines the keys in loinc "tissue". A complete match receives
     * 20 points. A partial match receives points based on how many keys are
     * matched.
     * @return score
     */
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

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(this.score);
        String[] id_string = this.id.split("/");
        builder.append("\t\t" + id_string[id_string.length - 1]);
        builder.append("\t\t" + this.label);
        /**
        int count_label_lengh = this.label.length();
        while (count_label_lengh < 80) {
            builder.append(" ");
            count_label_lengh++;
        }
         **/
        if(this.definition != null){
            builder.append("\t\t" + this.definition);
        } else {
            builder.append("\t\t");
        }
        return builder.toString();
    }
}

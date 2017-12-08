package org.monarchinitiative.loinc2hpo.loinc;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import java.util.Set;

public enum Unit {



    MMOL_PER_LITER("mmol/l","MMOL/L");

    /** The set of synonyms that denote this kind of unit */
    private Set<String> labels;


    private Unit(String ...strings) {
        labels = Sets.newHashSet(strings);
    }



    public static ImmutableMap<String,Unit> getUnitMap() {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (Unit unit : Unit.values()) {
            for (String lab : unit.labels) {
                builder.put(lab,unit);
            }
        }
        return builder.build();
    }


}

package org.monarchinitiative.loinc2hpo.loinc;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * An enumeration of units used in LOINC. New units should be defined together with a list of synonyms.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.0.1
 */
public enum Unit {

    MMOL_PER_LITER("mmol/l","MMOL/L");

    /** The set of synonyms that denote this kind of unit */
    private Set<String> labels;

    /**
     * @param synonyms List of synonyms that denote this kind of unit.
     */
    private Unit(String ...synonyms) {
        labels = Sets.newHashSet(synonyms);
    }



    public static ImmutableMap<String,Unit> getUnitMap() {
        ImmutableMap.Builder<String,Unit> builder = new ImmutableMap.Builder();
        for (Unit unit : Unit.values()) {
            for (String lab : unit.labels) {
                builder.put(lab,unit);
            }
        }
        return builder.build();
    }


}

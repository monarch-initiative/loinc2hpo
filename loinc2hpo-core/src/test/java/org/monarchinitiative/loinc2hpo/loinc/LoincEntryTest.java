package org.monarchinitiative.loinc2hpo.loinc;

import com.google.common.collect.ImmutableMap;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.SharedResourceCollection;

public class LoincEntryTest {

    static ResourceCollection resourceCollection = SharedResourceCollection.resourceCollection;;

    @Test
    @Disabled
    public void unspecifiedSpecifiman() throws Exception {
        ImmutableMap<LoincId,LoincEntry> loincEntryList = resourceCollection.loincEntryMap();
        loincEntryList.values().stream()
                .filter(entry -> entry.getLongName().toLowerCase().contains("unspecified "))
                .map(entry -> entry.getLOINC_Number())
                .forEach(System.out::println);
    }

}
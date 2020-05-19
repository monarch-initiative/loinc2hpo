package org.monarchinitiative.loinc2hpo.loinc;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Loinc2HpoAnnotationCsvEntryTest {


    @Test
    public void importAnnotations() throws Exception {
        String annotationPath = this.getClass().getClassLoader().getResource("annotations.tsv").getPath();
        List<Loinc2HpoAnnotationCsvEntry> entries = Loinc2HpoAnnotationCsvEntry.importAnnotations(annotationPath);
        assertTrue(entries.size() > 100);
    }

}
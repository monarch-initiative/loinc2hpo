package org.monarchinitiative.loinc2hpocore.loinc;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotationCsvEntry;
import org.monarchinitiative.loinc2hpocore.io.Loinc2HpoAnnotationParser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Loinc2HpoAnnotationCsvEntryTest {

    @Test
    void importAnnotations() {
        String annotationPath = this.getClass().getClassLoader().getResource("annotations.tsv").getPath();
        List<Loinc2HpoAnnotationCsvEntry> entries = Loinc2HpoAnnotationParser.load(annotationPath);
        assertTrue(entries.size() > 100);
    }

    @Test
    void testToString() {
        Loinc2HpoAnnotationCsvEntry entry = new Loinc2HpoAnnotationCsvEntry(
                "123.3", "Qn", "FHIR", "L", "HP:001", "false", "2020-01-02",
                "JAX:azhang", null, null, "0.1", "true", "");
        String expected = "123.3\tQn\tFHIR\tL\tHP:001\tfalse\t2020-01-02\tJAX" +
                ":azhang\tNA\tNA\t0.1\ttrue\tNA";
        assertEquals(expected, entry.toString());
    }
}
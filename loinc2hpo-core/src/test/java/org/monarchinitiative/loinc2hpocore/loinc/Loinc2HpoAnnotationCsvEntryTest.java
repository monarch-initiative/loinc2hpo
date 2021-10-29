package org.monarchinitiative.loinc2hpocore.loinc;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotationEntryLEGACY;
import org.monarchinitiative.loinc2hpocore.io.Loinc2HpoAnnotationParserLEGACY;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Loinc2HpoAnnotationCsvEntryTest {

    @Test
    void importAnnotations() {
        String annotationPath = this.getClass().getClassLoader().getResource("annotations.tsv").getPath();
       // List<Loinc2HpoAnnotationEntryLEGACY> entries = Loinc2HpoAnnotationParserLEGACY.load(annotationPath);
       // assertTrue(entries.size() > 100);
    }

   /* @Test
    void testToString() {
        Loinc2HpoAnnotationEntryLEGACY entry = new Loinc2HpoAnnotationEntryLEGACY(
                "123.3", "Qn",  "L", "HP:001", "false", "2020-01-02",
                "JAX:azhang", null, null, "0.1", "true", "");
        String expected = "123.3\tQn\tFHIR\tL\tHP:001\tfalse\t2020-01-02\tJAX" +
                ":azhang\tNA\tNA\t0.1\ttrue\tNA";
        assertEquals(expected, entry.toString());
    }

    */
}
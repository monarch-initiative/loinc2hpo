package org.monarchinitiative.loinc2hpocore.annotation;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.model.Loinc2HpoAnnotation;
import org.monarchinitiative.loinc2hpocore.model.ShortCode;
import org.monarchinitiative.loinc2hpocore.model.LoincAnnotation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.monarchinitiative.loinc2hpocore.model.Loinc2HpoAnnotation.outcomes2LoincAnnotation;

public class Loinc2HpoAnnotationTest {

    private static Loinc2HpoAnnotation fromStringList(String [] fields) {
        String line = String.join("\t",fields);
        return Loinc2HpoAnnotation.fromAnnotationLine(line);
    }

    private static final String [] fields1 = {"6047-5",	"Qn", "N", "HP:0410235", ".", "OHSU:JP[2018-10-10]", "."};
    private static final String [] fields2 = {"6047-5",	"Qn", "H", "HP:0410235", ".", "OHSU:JP[2018-10-10]", "."};

    private final static Loinc2HpoAnnotation normalAnnot = fromStringList(fields1);
    private final static Loinc2HpoAnnotation highAnnot = fromStringList(fields2);



    @Test
    void testNormalAbnormalPair() {
        List<Loinc2HpoAnnotation> outcomelist= List.of(normalAnnot, highAnnot);
        assertEquals(2, outcomelist.size());
        LoincAnnotation loincAnnotation = outcomes2LoincAnnotation(outcomelist);
        assertNotNull(loincAnnotation);
    }

    @Test
    void testCreationOfTsv1() {
        String expectedTsvLine = String.join("\t", fields1);
        assertEquals(expectedTsvLine, normalAnnot.toTsv());
    }

    @Test
    void testCreationOfTsv2() {
        String expectedTsvLine = String.join("\t", fields2);
        assertEquals(expectedTsvLine, highAnnot.toTsv());
    }

    /**
     * This checks that the short form of the outcode code
     * ({@link ShortCode}) is use for output.
     */
    @Test
    void checkExportOfOrdNeg() {
        String [] fields = {"4622-7", "Ord", "NEG", "HP:0011902", ".", "HPO:nvasilevsky[2019-06-14]", "."};
        Loinc2HpoAnnotation negAnnot = fromStringList(fields);
        String expectedTsvLine = String.join("\t", fields);
        assertEquals(expectedTsvLine, negAnnot.toTsv());
    }




}

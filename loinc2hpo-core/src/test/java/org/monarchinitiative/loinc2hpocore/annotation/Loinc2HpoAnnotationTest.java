package org.monarchinitiative.loinc2hpocore.annotation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.monarchinitiative.loinc2hpocore.annotation.Loinc2HpoAnnotation.outcomes2LoincAnnotation;

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


}

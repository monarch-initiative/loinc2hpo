package org.monarchinitiative.loinc2hpo.util;

import org.apache.jena.rdf.model.Model;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class SparqlQueryTest {

    static Model model;

    //@BeforeClass
    public static void initializeModel() {
        String hpo = SparqlQuery.class.getResource("/hp.owl").getPath(); //need '/' to get a resource file
        System.out.println("hpo path: " + hpo);
        model = SparqlQuery.getOntologyModel(hpo);
    }

    @Test
    public void testbuildStandardQuery() {
        String test1 = "Testosterone";
        System.out.print(SparqlQuery.buildStandardQuery(test1));
    }

    @Test
    public void testBuildLooseQuery() {
        String test = "Testosterone";
        System.out.println(SparqlQuery.buildLooseQuery(test));
    }

    @Test
    public void testQuery1() {
        String loinc_name = "Testosterone Free [Mass/volume] in Serum or Plasma";
        SparqlQuery.query(loinc_name, model);
    }

    @Test
    public void testParameters() {
        String test = "Testosterone Free";
        String[] test_words = SparqlQuery.parameters(test);
        assertEquals("Testosterone", test_words[0]);
        assertEquals(2, test_words.length);

        test = "Aldolase";
        test_words = SparqlQuery.parameters(test);
        assertEquals(1, test_words.length);

        test = "Other cells/100 leukocytes";
        test_words = SparqlQuery.parameters(test);
        assertEquals(4, test_words.length);
        assertEquals("cells", test_words[1]);
        assertEquals("100", test_words[2]);

        test = "Streptococcus.beta-hemolytic";
        test_words = SparqlQuery.parameters(test);
        assertEquals(3, test_words.length);
        assertEquals("beta", test_words[1]);

        test = "Hemoglobin S/Hemoglobin.total";
        test_words = SparqlQuery.parameters(test);
        assertEquals(4, test_words.length);
    }

    @Test
    public void testTrimS() {
        String s = "Zinc";
        assertEquals("Zinc", SparqlQuery.trimS(s));

        s = "Cells";
        assertEquals("Cell", SparqlQuery.trimS(s));

        s = "exocytosis";
        assertEquals("exocytosi", SparqlQuery.trimS(s));
    }
}
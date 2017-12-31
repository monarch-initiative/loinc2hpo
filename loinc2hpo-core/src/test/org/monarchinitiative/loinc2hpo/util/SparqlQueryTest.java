package org.monarchinitiative.loinc2hpo.util;

import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class SparqlQueryTest {

    static Model model;


    //@BeforeClass
    public static void initializeModel() {
        //String hpo = SparqlQuery.class.getResource("/hp.owl").getPath();
        String hpo = SparqlQuery.class.getResource("/hp.obo").getPath();
        //need '/' to get a resource file
        System.out.println("hpo path: " + hpo);
        model = SparqlQuery.getOntologyModel(hpo);
    }

    @Test
    public void testinitializeModel() {
        //String hpo = SparqlQuery.class.getResource("/hp.owl").getPath();
        String hpo = SparqlQuery.class.getResource("/hp.obo").getPath();
        //need '/' to get a resource file
        System.out.println("hpo path: " + hpo);
        model = SparqlQuery.getOntologyModel(hpo);
    }
    @Test
    public void testbuildStandardQueryWithSingleKey() {
        String test1 = "Testosterone";
        System.out.print(SparqlQuery.buildStandardQueryWithSingleKey(test1));
    }

    @Test
    public void testBuildLooseQueryWithSingleKey() {
        String test = "Testosterone";
        System.out.println(SparqlQuery.buildLooseQueryWithSingleKey(test));
    }

    @Test
    public void testBuildLooseQueryWithMultiKeys() {
        String[] keys = new String[]{"excretion", "urine", "acid", "pH"};
        System.out.println(SparqlQuery.buildLooseQueryWithMultiKeys(Arrays.asList(keys)));
        keys = new String[]{"chronic", "kidney", "disease"};
        System.out.println(SparqlQuery.buildLooseQueryWithMultiKeys(Arrays.asList(keys)));
    }

    @Test
    public void testBuildStandardQueryWithMultiKeys() {
        String[] keys = new String[]{"excretion", "urine", "acid", "pH"};
        System.out.println(SparqlQuery.buildStandardQueryWithMultiKeys(Arrays.asList(keys)));
        keys = new String[]{"chronic", "kidney", "disease"};
        System.out.println(SparqlQuery.buildStandardQueryWithMultiKeys(Arrays.asList(keys)));
    }

    @Test
    public void testQueryWithOneKey() {
        String key = "Testosterone";
        String looseQueryString = SparqlQuery.buildLooseQueryWithSingleKey(key);
        String standardQueryString = SparqlQuery.buildStandardQueryWithSingleKey(key);
        System.out.println("loose query:\n" + looseQueryString);
        System.out.println("\n\nstandard query:\n" + standardQueryString);
        Query looseQuery = QueryFactory.create(looseQueryString);
        Query standardQuery = QueryFactory.create(standardQueryString);
        List<HPO_Class_Found> results_loose = SparqlQuery.query(looseQuery, model, null);
        List<HPO_Class_Found> results_standard = SparqlQuery.query(standardQuery, model, null);
        System.out.println(results_loose.size() + " HPO terms are found!");
        for(HPO_Class_Found hpo : results_loose) {
            System.out.println(hpo.getLabel() + "\t" + hpo.getId() + "\n" + hpo.getDefinition());
        }
        assertEquals(16, results_loose.size()); //interesting that this program identifies 16 classes;
                                                            // while the same query finds 14 in command line
                                                            //reason: command line uses hp.owl; this program builds a model from hp.owl(?)
        System.out.println(results_standard.size()+ " HPO terms are found!");
        assertEquals(13, results_standard.size());
    }

    @Test
    public void testQueryWithMultiKeys() {

        String[] keys = new String[]{"chronic", "kidney", "disease"};
        String looseQueryString = SparqlQuery.buildLooseQueryWithMultiKeys(Arrays.asList(keys));
        String standardQueryString = SparqlQuery.buildStandardQueryWithMultiKeys(Arrays.asList(keys));
        Query looseQuery = QueryFactory.create(looseQueryString);
        Query standardQuery = QueryFactory.create(standardQueryString);
        List<HPO_Class_Found> itr_loose = SparqlQuery.query(looseQuery, model, null);
        List<HPO_Class_Found> itr_standard = SparqlQuery.query(standardQuery, model, null);

        System.out.println(itr_loose.size() + " HPO terms are found!");
        //assertEquals(7, itr_loose.size());

        System.out.println(itr_standard.size() + " HPO terms are found!");
        //assertEquals(5, itr_standard.size());

    }

    @Test
    public void tests(){
        testQueryWithOneKey();
        testQueryWithMultiKeys();
    }

    @Test
    public void testQuery_auto() {
        //String loinc_name = "Testosterone Free [Mass/volume] in Serum or Plasma";
        //SparqlQuery.query_auto(loinc_name);

        //String loinc_name = "Erythrocyte distribution width [Ratio] in blood or serum by Automated count";
        //String loinc_name = "Carbon dioxide, total [Moles/volume] in Serum or Plasma";
        //List<HPO_Class_Found> hpo_clsses_found = new ArrayList<>();
        /**
        System.out.println("Find Carbon dioxide, total [Moles/volume] in Serum or Plasma: ");
        SparqlQuery.query_auto("Carbon dioxide, total [Moles/volume] in Serum or Plasma");
        System.out.println("Find Anion gap 3 in Serum or Plasma: ");
        SparqlQuery.query_auto("Anion gap 3 in Serum or Plasma");
         **/
        System.out.println("Find \"Potassium [Moles/volume] in Serum or Plasma\": ");
        List<HPO_Class_Found> hpo_clsses_found = SparqlQuery.query_auto("Potassium [Moles/volume] in Serum or Plasma");
        for (HPO_Class_Found HPO_class : hpo_clsses_found) {
            StringBuilder outContent = new StringBuilder();
            //outContent.append(newline);
            //outContent.append("\t");
            outContent.append(HPO_class.getScore() + "\t");
            outContent.append(HPO_class.getId() + "\t");
            outContent.append(HPO_class.getLabel() + "\t");
            if (HPO_class.getDefinition() != null)  {
                outContent.append(HPO_class.getDefinition());
            }
            outContent.append("\n");
            System.out.println(outContent.toString());
        }
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
/**
    @Test
    public void testTrimS() {
        String s = "Zinc";
        assertEquals("Zinc", SparqlQuery.trimS(s));

        s = "Cells";
        assertEquals("Cell", SparqlQuery.trimS(s));

        s = "exocytosis";
        assertEquals("exocytosi", SparqlQuery.trimS(s));
    }
**/
    @Test
    public void testGetChildren() {
        String current = "http://purl.obolibrary.org/obo/HP_0012598";
        List<HPO_Class_Found> results = SparqlQuery.getChildren(current);
        assertEquals(3, results.size());
        for (HPO_Class_Found hpo_term : results) {
            System.out.println(hpo_term.getId());
            System.out.println(hpo_term.getLabel());
            if(hpo_term.getDefinition() != null) {
                System.out.println(hpo_term.getDefinition());
            }
        }

        current = "http://purl.obolibrary.org/obo/HP_0012100";
        results = SparqlQuery.getChildren(current);
        assertEquals(2, results.size());
        for (HPO_Class_Found hpo_term : results) {
            System.out.println(hpo_term.getId());
            System.out.println(hpo_term.getLabel());
            if(hpo_term.getDefinition() != null) {
                System.out.println(hpo_term.getDefinition());
            }
        }

        current = "http://purl.obolibrary.org/obo/HP_0012101";
        results = SparqlQuery.getChildren(current);
        assertEquals(0, results.size());
        for (HPO_Class_Found hpo_term : results) {
            System.out.println(hpo_term.getId());
            System.out.println(hpo_term.getLabel());
            if(hpo_term.getDefinition() != null) {
                System.out.println(hpo_term.getDefinition());
            }
        }

        current = "http://purl.obolibrary.org/obo/HP_0004364";
        results = SparqlQuery.getChildren(current);
        assertEquals(8, results.size());
        for (HPO_Class_Found hpo_term : results) {
            System.out.println(hpo_term.getId());
            System.out.println(hpo_term.getLabel());
            if(hpo_term.getDefinition() != null) {
                System.out.println(hpo_term.getDefinition());
            }
        }
    }

    @Test
    public void testGetParents(){
        String current = "http://purl.obolibrary.org/obo/HP_0012598";
        List<HPO_Class_Found> results = SparqlQuery.getParents(current);
        //assertEquals(1, results.size());
        for (HPO_Class_Found hpo_term : results) {
            System.out.println(hpo_term.getId());
            System.out.println(hpo_term.getLabel());
            if(hpo_term.getDefinition() != null) {
                System.out.println(hpo_term.getDefinition());
            }
        }

        current = "http://purl.obolibrary.org/obo/HP_0012100";
        results = SparqlQuery.getParents(current);
        //assertEquals(1, results.size());
        for (HPO_Class_Found hpo_term : results) {
            System.out.println(hpo_term.getId());
            System.out.println(hpo_term.getLabel());
            if(hpo_term.getDefinition() != null) {
                System.out.println(hpo_term.getDefinition());
            }
        }

        current = "http://purl.obolibrary.org/obo/HP_0012101";
        results = SparqlQuery.getParents(current);
        //assertEquals(1, results.size());
        for (HPO_Class_Found hpo_term : results) {
            System.out.println(hpo_term.getId());
            System.out.println(hpo_term.getLabel());
            if(hpo_term.getDefinition() != null) {
                System.out.println(hpo_term.getDefinition());
            }
        }

        current = "http://purl.obolibrary.org/obo/HP_0004364";
        results = SparqlQuery.getParents(current);
        //assertEquals(1, results.size());
        for (HPO_Class_Found hpo_term : results) {
            System.out.println(hpo_term.getId());
            System.out.println(hpo_term.getLabel());
            if(hpo_term.getDefinition() != null) {
                System.out.println(hpo_term.getDefinition());
            }
        }
    }

    @Test
    public void testRe() {

        String label = "Increased urinary potassium" ;
        String definition = "An increased concentration of potassium(1+) in the urine.";
        String loincname = "Potassium [Moles/volume] in Serum or Plasma";
        String total = label + " " + definition;

        String test = "Cardiac*";
        //assertEquals("cardiac*", test.toLowerCase());

        test = "potassium:chloride symporter activity";
        //assertEquals(false, test.matches(".*((increase*)|(decrease*)|(elevate*)|(reduc*)|(high*)|(low*)|(above)|(below)|(abnormal*)).*"));

        assertEquals(false, test.matches(".*(increase*|decrease*|elevate*|reduc*|high*|\"low*\").*"));
    }
}
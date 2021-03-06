package org.monarchinitiative.loinc2hpocore.util;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.sparql.HPO_Class_Found;
import org.monarchinitiative.loinc2hpocore.sparql.SparqlQuery;


import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class SparqlQueryTest {

    public static Model model;

    @BeforeAll
    public static void setup() {

        InputStream hpo =
                SparqlQueryTest.class.getResourceAsStream("/hp.owl");
        model = SparqlQuery.getOntologyModel(hpo);
        SparqlQuery.setHPOmodel(model);
    }


    @Test
    public void testinitializeModel() {
        assertNotNull(model);
    }

    @Test
    public void testbuildStandardQueryWithSingleKey() {
        String test1 = "Testosterone";
        String expected = "PREFIX xmlns: <http://purl.obolibrary.org/obo/hp.owl#> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX owl:<http://www.w3.org/2002/07/owl#> " +
                "PREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#> " +
                "PREFIX hsapdv: <http://purl.obolibrary.org/obo/hsapdv#> " +
                "PREFIX hp: <http://purl.obolibrary.org/obo/hp#> " +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX hp2: <http://purl.obolibrary.org/obo/hp.owl#> " +
                "PREFIX obo: <http://purl.obolibrary.org/obo/> " +
                "PREFIX dc: <http://purl.org/dc/elements/1.1/> " +
                "SELECT DISTINCT ?phenotype ?label ?definition  " +
                "WHERE {{?phenotype obo:IAO_0000115 ?definition .  " +
                "?phenotype rdfs:label ?label .  " +
                "FILTER (regex(?definition, \"Testosterone\", \"i\"))  " +
                "FILTER (regex(?definition, \"increase.*|decrease.*|elevat.*|reduc.*|high.*|low.*|above|below|abnormal.*\", \"i\"))} " +
                "UNION {?phenotype rdfs:label ?label .  " +
                "OPTIONAL {?phenotype obo:IAO_0000115 ?definition .}  " +
                "FILTER (regex(?label, \"Testosterone\", \"i\"))  " +
                "FILTER (regex(?label, \"increase.*|decrease.*|elevat.*|reduc.*|high.*|low.*|above|below|abnormal.*\", \"i\"))}}";
        assertEquals(expected, SparqlQuery.buildStandardQueryWithSingleKey(test1));
    }

    @Test
    public void testBuildLooseQueryWithSingleKey() {
        String test = "Testosterone";
        String expected = "PREFIX xmlns: <http://purl.obolibrary.org/obo/hp.owl#> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX owl:<http://www.w3.org/2002/07/owl#> " +
                "PREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#> " +
                "PREFIX hsapdv: <http://purl.obolibrary.org/obo/hsapdv#> " +
                "PREFIX hp: <http://purl.obolibrary.org/obo/hp#> " +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX hp2: <http://purl.obolibrary.org/obo/hp.owl#> " +
                "PREFIX obo: <http://purl.obolibrary.org/obo/> " +
                "PREFIX dc: <http://purl.org/dc/elements/1.1/> " +
                "SELECT DISTINCT ?phenotype ?label ?definition  " +
                "WHERE {{?phenotype obo:IAO_0000115 ?definition .  " +
                "?phenotype rdfs:label ?label .  " +
                "FILTER (regex(?definition, \"Testosterone\", \"i\"))  } " +
                "UNION {?phenotype rdfs:label ?label .  " +
                "OPTIONAL {?phenotype obo:IAO_0000115 ?definition .}  " +
                "FILTER (regex(?label, \"Testosterone\", \"i\"))  }}";
        assertEquals(expected, SparqlQuery.buildLooseQueryWithSingleKey(test));
    }

    @Test
    public void testBuildLooseQueryWithMultiKeys() {
        String[] keys = new String[]{"excretion", "urine", "acid", "pH"};
        //System.out.println(SparqlQuery.buildLooseQueryWithMultiKeys(Arrays.asList(keys)));
        assertNotNull(SparqlQuery.buildLooseQueryWithMultiKeys(Arrays.asList(keys)));
        keys = new String[]{"chronic", "kidney", "disease"};
        //System.out.println(SparqlQuery.buildLooseQueryWithMultiKeys(Arrays.asList(keys)));
        assertNotNull(SparqlQuery.buildLooseQueryWithMultiKeys(Arrays.asList(keys)));
    }

    @Test
    public void testBuildStandardQueryWithMultiKeys() {
        String[] keys = new String[]{"excretion", "urine", "acid", "pH"};
        //System.out.println(SparqlQuery.buildStandardQueryWithMultiKeys(Arrays.asList(keys)));
        assertNotNull(SparqlQuery.buildLooseQueryWithMultiKeys(Arrays.asList(keys)));
        keys = new String[]{"chronic", "kidney", "disease"};
        //System.out.println(SparqlQuery.buildStandardQueryWithMultiKeys(Arrays.asList(keys)));
        assertNotNull(SparqlQuery.buildLooseQueryWithMultiKeys(Arrays.asList(keys)));
    }

    @Test
    public void testQueryWithOneKey() {
        String key = "Testosterone";
        String looseQueryString = SparqlQuery.buildLooseQueryWithSingleKey(key);
        String standardQueryString = SparqlQuery.buildStandardQueryWithSingleKey(key);
        //System.out.println("loose query:\n" + looseQueryString);
        //System.out.println("\n\nstandard query:\n" + standardQueryString);
        Query looseQuery = QueryFactory.create(looseQueryString);
        Query standardQuery = QueryFactory.create(standardQueryString);
        assertNotNull(model);
        List<HPO_Class_Found> results_loose = SparqlQuery.query(looseQuery, model, null);
        List<HPO_Class_Found> results_standard = SparqlQuery.query(standardQuery, model, null);

        assertEquals(14, results_loose.size()); //interesting that this program identifies 16 classes;
                                                            // while the same query finds 14 in command line
                                                            //reason: command line uses hp.owl; this program builds a model from hp.owl(?)
        //reason: more likely has something to do with what kind of model is generated from hp.owl
        //System.out.println(results_standard.size()+ " HPO terms are found!");
        assertEquals(9, results_standard.size());
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

        //System.out.println(itr_loose.size() + " HPO terms are found!");
        assertEquals(7, itr_loose.size());

        //System.out.println(itr_standard.size() + " HPO terms are found!");
        assertEquals(5, itr_standard.size());

    }

    @Test
    public void testQuery_auto() {
        //String loinc_name = "Testosterone Free [Mass/volume] in Serum or Plasma";
        //SparqlQuery.query_auto(loinc_name);

        //String loinc_name = "Erythrocyte distribution width [Ratio] in blood or serum by Automated count";
        //String loinc_name = "Carbon dioxide, total [Moles/volume] in Serum or Plasma";
        //List<HPO_Class_Found> hpo_clsses_found = new ArrayList<>();

        //System.out.println("Find \"Potassium [Moles/volume] in Serum or Plasma\": ");
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
            //System.out.println(outContent.toString());
        }
    }


    @Test
    public void testGetChildren() {
        String current = "http://purl.obolibrary.org/obo/HP_0012598";
        List<HPO_Class_Found> results = SparqlQuery.getChildren(current);
        assertEquals(3, results.size());

        current = "http://purl.obolibrary.org/obo/HP_0012100";
        results = SparqlQuery.getChildren(current);
        assertEquals(2, results.size());

        current = "http://purl.obolibrary.org/obo/HP_0012101";
        results = SparqlQuery.getChildren(current);
        assertEquals(0, results.size());

        current = "http://purl.obolibrary.org/obo/HP_0004364";
        results = SparqlQuery.getChildren(current);
        assertEquals(8, results.size());
    }

    @Test
    public void testGetParents(){
        String current = "http://purl.obolibrary.org/obo/HP_0012598";
        List<HPO_Class_Found> results = SparqlQuery.getParents(current);
        assertEquals(1, results.size());

        current = "http://purl.obolibrary.org/obo/HP_0012100";
        results = SparqlQuery.getParents(current);
        assertEquals(1, results.size());

        current = "http://purl.obolibrary.org/obo/HP_0012101";
        results = SparqlQuery.getParents(current);
        assertEquals(1, results.size());

        current = "http://purl.obolibrary.org/obo/HP_0004364";
        results = SparqlQuery.getParents(current);
        assertEquals(1, results.size());
    }

    @Test
    public void testRe() {
        String label = "Increased urinary potassium" ;
        String definition = "An increased concentration of potassium(1+) in the urine.";
        String loincname = "Potassium [Moles/volume] in Serum or Plasma";
        String total = label + " " + definition;

        String test = "potassium:chloride symporter activity";
        //assertEquals(false, test.matches(".*((increase*)|(decrease*)|(elevate*)|(reduc*)|(high*)|(low*)|(above)|(below)|(abnormal*)).*"));

        assertFalse(test.matches(".*(increase*|decrease*|elevate*|reduc*|high*|\"low*\").*"));
    }
}
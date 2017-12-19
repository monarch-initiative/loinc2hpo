package org.monarchinitiative.loinc2hpo.util;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.impl.OntModelImpl;
import org.apache.jena.query.*;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

public class SparqlQuery {

    private final String parameter;
    private final String tissue;
    private final String HPO = this.getClass().getResource("hp.owl").getFile();

    public SparqlQuery(String parameter, String tissue) {
        this.parameter = parameter;
        this.tissue = tissue;
    }

    public static Model getOntologyModel(String ontologyFile) {
        Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {
            InputStream in = FileManager.get().open(ontologyFile);
            try {
                model.read(in, null);
                System.out.println("model created");
            } catch (Exception e) {
                System.out.println("cannot read in data to model");
            }
        } catch (JenaException je) {
            System.out.println("cannot open hpo.owl");
        }
        return model;
    }

    public static ResultSet query(Model model, String queryString) {
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            return qexec.execSelect();
        }
    }


    public static void main(String[] args) {
        String hpo = SparqlQuery.class.getResource("/hp.owl").getPath(); //need '/' to get a resource file
        System.out.println("hpo path: " + hpo);
        Model model = getOntologyModel(hpo);
        String prefix = "PREFIX xmlns: <http://purl.obolibrary.org/obo/hp.owl#> "+
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX owl:<http://www.w3.org/2002/07/owl#> " +
                "PREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#> " +
                "PREFIX hsapdv: <http://purl.obolibrary.org/obo/hsapdv#> " +
                "PREFIX hp: <http://purl.obolibrary.org/obo/hp#> " +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX hp2: <http://purl.obolibrary.org/obo/hp.owl#> " +
                "PREFIX obo: <http://purl.obolibrary.org/obo/> " +
                "PREFIX dc: <http://purl.org/dc/elements/1.1/> ";
        String selection = "SELECT DISTINCT ?phenotype ?label " +
                "WHERE {?phenotype obo:IAO_0000115 ?definition . " +
                "?phenotype rdfs:label ?label . ";

        String parameter = "Testosterone";

        String filter =  String.format("FILTER (regex(?definition, \"%s\", \"i\") || regex(?label, \"%s\", \"i\")) " +
                "FILTER (regex(?definition, \"increased|decreased|elevated|reduced|high|low|above|below\", \"i\") || regex(?label, \"increased|decreased|elevated|reduced|high|low|above|below|hyper|hypo\", \"i\"))\n" +
                "} ", parameter, parameter);

        String queryText =  prefix + selection + filter;

        Query query = QueryFactory.create(queryText);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        Iterator<QuerySolution> results = qexec.execSelect();
        int count = 0;
        while (results.hasNext()) {
            count++;
            System.out.println(results.next().toString());
        }
System.out.println(count + " results are found!");
    }

}

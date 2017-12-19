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

    public static void main(String[] args) {
        String hpo = SparqlQuery.class.getResource("hp.owl").getPath();
        System.out.println("hpo path: " + hpo);
        Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {
            InputStream in = FileManager.get().open(hpo);
            try {
                model.read(in, null);
                System.out.println("model created");
            } catch (Exception e) {
                System.out.println("cannot read in data to model");
            }
        } catch (JenaException je) {
            System.out.println("cannot open hpo.owl");
        }
        /**
        String queryText = "SELECT * WHERE {?s ?p ?o} LIMIT 10";
        Query query = QueryFactory.create(queryText);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        Iterator<QuerySolution> results = qexec.execSelect();
        if (results.hasNext()) {
            System.out.println(results.next().toString());
        }
         **/
    }



}

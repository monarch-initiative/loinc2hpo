package org.monarchinitiative.loinc2hpo.util;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

import java.io.*;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class SparqlQuery {

    private static final String hpo = SparqlQuery.class.getResource("/hp.owl").getPath(); //need '/' to get a resource file
    private static boolean modelCreated = false;
    private static Model model;
    private static final String HPO_PREFIX = "PREFIX xmlns: <http://purl.obolibrary.org/obo/hp.owl#> "+
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
    private static String DISPLAY = "SELECT DISTINCT ?phenotype ?label ?definition ";
    private static String[] invalid_words = new String[]
            {"mean", "in", "of", "identified", "cell", "conjugated", "other", "virus", "normal"};
    //private static HashSet<String> invalid_words= new HashSet<>();

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

    private static void createHPOModel() {
        model = getOntologyModel(hpo);
        modelCreated = true;
    }

    //check classes that contain the parameter and has modifier in label / definition
    public static String buildStandardQuery(String parameter_single_word) {

        StringBuilder standardQuery = new StringBuilder();
        standardQuery.append(HPO_PREFIX);
        standardQuery.append(DISPLAY);
        String condition = String.format(" WHERE {" +
                "{?phenotype obo:IAO_0000115 ?definition . " +
                " ?phenotype rdfs:label ?label . " +
                " FILTER (regex(?definition, \"%s\", \"i\")) " +
                " FILTER (regex(?definition, \"increase*|decrease*|elevate*|reduc*|high*|low*|above|below|abnormal*\", \"i\"))} " +
                "UNION" +
                " {?phenotype rdfs:label ?label . " +
                " OPTIONAL {?phenotype obo:IAO_0000115 ?definition .} " +
                " FILTER (regex(?label, \"%s\", \"i\")) " +
                " FILTER (regex(?label, \"increase*|decrease*|elevate*|reduc*|high*|low*|above|below|hyper*|hypo*|abnormal*\", \"i\"))}" +
                "}", parameter_single_word, parameter_single_word);
        standardQuery.append(condition);
        return standardQuery.toString();
    }

    //check classes containing the parameter
    public static String buildLooseQuery(String parameter_single_word) {

        StringBuilder looseQuery = new StringBuilder();
        looseQuery.append(HPO_PREFIX);
        looseQuery.append(DISPLAY);
        String condition = String.format(" WHERE {" +
                "{?phenotype obo:IAO_0000115 ?definition . " +
                " ?phenotype rdfs:label ?label . " +
                " FILTER (regex(?definition, \"%s\", \"i\")) " +
                " } " +
                "UNION" +
                " {?phenotype rdfs:label ?label . " +
                " OPTIONAL {?phenotype obo:IAO_0000115 ?definition .} " +
                " FILTER (regex(?label, \"%s\", \"i\")) " +
                " }" +
                "}", parameter_single_word, parameter_single_word);
        looseQuery.append(condition);
        return looseQuery.toString();

    }

    public static List<HPO_Class_Found> query(String loincLongCommonName, Model hpomodel) {

        if(!modelCreated) {
            createHPOModel();
        }
        List<HPO_Class_Found> HPO_classes_found = new ArrayList<>();
        //first parse the loinc long common name
        LoincCodeClass loincClass = LoincLongNameParser.parse(loincLongCommonName);
        String[] parameters = parameters(loincClass.getLoincParameter());
        //the parameter could be more than one word
        //use the first word to do initial search
        String queryString = buildStandardQuery(parameters[0]);
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, hpomodel);
        Iterator<QuerySolution> results = qexec.execSelect();
        int count = 0;
        count = addFoundClasses(HPO_classes_found, results, loincClass);
        System.out.println(count + " results are found!");

        if (count < 3) { //loose criteria
            HPO_classes_found = new ArrayList<>();
            queryString = buildLooseQuery(parameters[0]);
            query = QueryFactory.create(queryString);
            qexec = QueryExecutionFactory.create(query, hpomodel);
            results = qexec.execSelect();
            count = 0;
            count = addFoundClasses(HPO_classes_found, results, loincClass);
            System.out.println(count + " results are found!");

        }

        if (count < 3) { //use another word and standard query

        }

        if (count < 3) { //use another word and loose query

        }

        if (count > 20) { //too many, add another criteria: tissue--pay attention to synonyms of tissue
                            //e.g. blood = serum = plasma
                            //     urine = urinary
                            //     kidney = renal

        }
        return HPO_classes_found;

    }

    public static List<HPO_Class_Found> query_with_multiple_keywords(String[] keys) {
        if(!modelCreated) {
            createHPOModel();
        }

        return null;
    }


    public static List<HPO_Class_Found> getChildren(String HPO_class_URL) {
        if(!modelCreated) {
            createHPOModel();
        }
        StringBuilder childrenQuery = new StringBuilder();
        childrenQuery.append(HPO_PREFIX);
        childrenQuery.append(DISPLAY);
        String condition = String.format(" WHERE {" +
                "?phenotype rdfs:subClassOf <%s> . " +
                "?phenotype rdfs:label ?label . " +
                "OPTIONAL {?phenotype obo:IAO_0000115 ?definition} .} ", HPO_class_URL);
        childrenQuery.append(condition);
        Query query = QueryFactory.create(childrenQuery.toString());
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        Iterator<QuerySolution> results = qexec.execSelect();
        List<HPO_Class_Found> HPO_classes_found = new ArrayList<>();
        int count = 0;
        count = addFoundClasses(HPO_classes_found, results, null);
        System.out.println(count + " results are found!");
        return HPO_classes_found;
    }

    public static List<HPO_Class_Found> getParents(String HPO_class_URL) {
        if(!modelCreated) {
            createHPOModel();
        }
        StringBuilder parentQuery = new StringBuilder();
        parentQuery.append(HPO_PREFIX);
        parentQuery.append(DISPLAY);
        String condition = String.format(" WHERE {" +
                "<%s> rdfs:subClassOf ?phenotype . " +
                "?phenotype rdfs:label ?label . " +
                "OPTIONAL {?phenotype obo:IAO_0000115 ?definition} .} ", HPO_class_URL);
        parentQuery.append(condition);
        Query query = QueryFactory.create(parentQuery.toString());
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        Iterator<QuerySolution> results = qexec.execSelect();
        List<HPO_Class_Found> HPO_classes_found = new ArrayList<>();
        int count = 0;
        count = addFoundClasses(HPO_classes_found, results, null);
        System.out.println(count + " results are found!");
        return HPO_classes_found;
    }


    public static int addFoundClasses (List<HPO_Class_Found> HPO_classes_found, Iterator<QuerySolution> results, LoincCodeClass loincClass) {
        int count = 0;
        while (results.hasNext()) {
            count++;
            QuerySolution next = results.next();
            RDFNode pNode = next.get("phenotype");
            RDFNode lNode = next.get("label");
            RDFNode dNode = next.get("definition");

            if (pNode.isResource() && lNode.isLiteral() && dNode != null && dNode.isLiteral()) {
                HPO_classes_found.add(new HPO_Class_Found(pNode.toString(), lNode.toString(), dNode.toString(), loincClass));
            } else if (pNode.isResource() && lNode.isLiteral() && dNode == null) {
                HPO_classes_found.add(new HPO_Class_Found(pNode.toString(), lNode.toString(), null, loincClass));
            } else {
                //do nothing; not going to happen
            }
        }
        return count;
    }

    public static String[] parameters(String loincparameter) {
        //split the loinc parameter into individual words
        String[] words = loincparameter.split("\\W");
        return words;
    }

    public static boolean valid(String word) { //test whether a word should be used in building a query

        if (word == null) {
            return false;
        }
        if (word.length() == 1) {
            return false;
        }

        try {
            int value = Integer.parseInt(word);
            return false;
        } catch (Exception e) {
            HashSet<String> invalid = new HashSet<>();
            invalid.addAll(Arrays.asList(invalid_words));
            return !invalid.contains(word);
        }

    }

    public static String trimS(String s) {
        String newString;
        if (s.endsWith("s") && s.length() > 2) {
            newString = s.substring(0, s.length() - 1);
        } else {
            newString = s;
        }
        return newString;
    }




    public static void main(String[] args) {
        //String hpo = SparqlQuery.class.getResource("/hp.owl").getPath(); //need '/' to get a resource file
        //System.out.println("hpo path: " + hpo);
        //Model model = getOntologyModel(hpo);

        String hpo = SparqlQuery.class.getResource("/hp.owl").getPath(); //need '/' to get a resource file
        Model model = SparqlQuery.getOntologyModel(hpo);

        Path path = Paths.get(SparqlQuery.class.getResource("/loinc_most_frequent.csv").getFile());
        String newline;
        try (InputStream input = Files.newInputStream(path)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter writer = new PrintWriter("loinc-hpo-mapping.tsv");
            reader.readLine();
            writer.write("Loinc Long Common Name\tCandidate HPO Class URI\tCandidate HPO Class Label\tCandidate HPO Class Definition\n");
            while ((newline = reader.readLine()) != null) {
                List<HPO_Class_Found> hpo_clsses_found = SparqlQuery.query(newline, model);
                if (hpo_clsses_found.size() > 0 && hpo_clsses_found.size() < 20) {
                    for (HPO_Class_Found HPO_class : hpo_clsses_found) {
                        StringBuilder outContent = new StringBuilder();
                        outContent.append(newline);
                        outContent.append("\t");
                        outContent.append(HPO_class.getId() + "\t");
                        outContent.append(HPO_class.getLabel() + "\t");
                        if (HPO_class.getDefinition() != null)  {
                            outContent.append(HPO_class.getDefinition());
                        }
                        outContent.append("\n");
                        writer.write(outContent.toString());
                    }
                } else if (hpo_clsses_found.size() == 0) {
                    writer.write(newline + "\tNo HPO classes are found\n ");
                } else {
                    writer.write(newline + "\tToo many (>=20) classes are found\n");
                }


            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

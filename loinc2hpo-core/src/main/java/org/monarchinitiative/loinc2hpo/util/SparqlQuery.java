package org.monarchinitiative.loinc2hpo.util;



import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.shared.AddDeniedException;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class SparqlQuery {

    //private static final String hpo = SparqlQuery.class.getResource("/hp" +
    //        ".owl").getPath(); //need '/' to get a resource file
    public static boolean modelCreated = false; //check whether the model for
    // hpo has been created
    public static Model model; //model of hp.owl for Sparql query
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
    private static final String DISPLAY = "SELECT DISTINCT ?phenotype ?label ?definition ";

    //public static final String modifier = "increase*|decrease*|elevate*|reduc*|high*|low*|above|below|abnormal*";
    public static final String modifier = "increase.*|decrease.*|elevat.*|reduc.*|high.*|low.*|above|below|abnormal.*";
    private static final Logger logger = LogManager.getLogger();

    /**
     * Create an ontology model from its owl file
     * @param path_to_ontology
     * @return the ontology model for query
     */
    public static Model getOntologyModel(String path_to_ontology) {
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {
            InputStream in = FileManager.get().open(path_to_ontology);
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

    /**
     * Create the HPO model
     */
    private static void createHPOModel(String pathToHpoOwl) {
        if (pathToHpoOwl==null) {
            logger.error("Could not retrieve hp.owl file -- did you download it from the Edit menu?");
            return;
        } else {
            logger.error("GOT HPO file at "+ pathToHpoOwl);
        }
        model = getOntologyModel(pathToHpoOwl);
        modelCreated = true;
    }


    /**
     * Build a standard sparql query from a single key. It searches for HPO classes that
     * have the key in class label or definition, AND the class should have a modifier
     * (increase/decrease/abnormal etc) in class label or definition.
     * @param single_word_key
     * @return a sparql query string to select HPO classes that
     */
    public static String buildStandardQueryWithSingleKey(String single_word_key) {

        StringBuilder standardQuery = new StringBuilder();
        standardQuery.append(HPO_PREFIX);
        standardQuery.append(DISPLAY);
        String condition = String.format(" WHERE {" +
                "{?phenotype obo:IAO_0000115 ?definition . " +
                " ?phenotype rdfs:label ?label . " +
                " FILTER (regex(?definition, \"%s\", \"i\")) " +
                " FILTER (regex(?definition, \"%s\", \"i\"))} " +
                "UNION" +
                " {?phenotype rdfs:label ?label . " +
                " OPTIONAL {?phenotype obo:IAO_0000115 ?definition .} " +
                " FILTER (regex(?label, \"%s\", \"i\")) " +
                " FILTER (regex(?label, \"%s\", \"i\"))}" +
                "}", single_word_key, modifier, single_word_key, modifier);
        standardQuery.append(condition);
        return standardQuery.toString();
    }

    /**
     * Build a loose sparql query from a single key. It simply searches for HPO classes that
     * have the key in class label or definition, WITHOUT the requirement for modifiers
     * (decrease/increase/abnormal etc)
     * @param single_word_key
     * @return a sparql query string to select HPO classes that
     */
    public static String buildLooseQueryWithSingleKey(String single_word_key) {

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
                "}", single_word_key, single_word_key);
        looseQuery.append(condition);
        return looseQuery.toString();

    }

    /**
     * Build a standard Sparql query from a list of keys. It searches for HPO classes that have
     * all the keys in class label or definition, AND the classes should have modifiers (increase/
     * decrease/abnormal etc) in class label or definition.
     * @param keys
     * @return
     */
    public static String buildStandardQueryWithMultiKeys(List<String> keys) { //provide multiple keywords for query
        if (keys == null) {
            throw new IllegalArgumentException("Key list is empty");
        }
        StringBuilder multiKeyQuery = new StringBuilder();
        multiKeyQuery.append(HPO_PREFIX);
        multiKeyQuery.append(DISPLAY);
        StringBuilder labelfilters = new StringBuilder();
        StringBuilder definitionfilters = new StringBuilder();
        for (String key : keys) {
            labelfilters.append(String.format("FILTER (regex(?label, \"%s\", \"i\")) ", key));
            //if a HPO class has a definition, then filter keys in concat(label, definition)
            //no need to ask all the keys appear in definition!
            definitionfilters.append(String.format("FILTER (regex(concat(?label, \" \", ?definition), \"%s\", \"i\")) ", key));
        }
        String condition = String.format(" WHERE " +
                "{" +
                " {?phenotype obo:IAO_0000115 ?definition . " +
                " ?phenotype rdfs:label ?label . " +
                " FILTER (regex(concat(?label, \" \", ?definition), \"%s\", \"i\")) " +
                " %s } " +
                "UNION" +
                " {?phenotype rdfs:label ?label . " +
                " OPTIONAL {?phenotype obo:IAO_0000115 ?definition .} " +
                " FILTER (regex(?label, \"%s\", \"i\"))" +
                " %s }" +
                "}", modifier, definitionfilters.toString(), modifier, labelfilters.toString());
        multiKeyQuery.append(condition);
        return multiKeyQuery.toString();
    }

    /**
     * Build a loose Sparql query from a list of keys. It searches for HPO classes that have all the keys
     * in class label or definition, without the requirement that the classes should have modifiers
     * (increase/decrease/abnormal etc) in class label or modifier.
     * @param keys
     * @return
     */
    public static String buildLooseQueryWithMultiKeys(List<String> keys) { //provide multiple keywords for query
        if (keys == null) {
            throw new IllegalArgumentException("Key list is empty");
        }
        StringBuilder multiKeyQuery = new StringBuilder();
        multiKeyQuery.append(HPO_PREFIX);
        multiKeyQuery.append(DISPLAY);
        StringBuilder labelfilters = new StringBuilder();
        StringBuilder definitionfilters = new StringBuilder();
        for (String key : keys) {
            labelfilters.append(String.format("FILTER (regex(?label, \"%s\", \"i\")) ", key));
            definitionfilters.append(String.format("FILTER (regex(concat(?label, \" \", ?definition), \"%s\", \"i\")) ", key));
        }
        String condition = String.format(" WHERE " +
                "{" +
                " {?phenotype obo:IAO_0000115 ?definition . " +
                " ?phenotype rdfs:label ?label . " +
                " %s } " +
                "UNION" +
                " {?phenotype rdfs:label ?label . " +
                " OPTIONAL {?phenotype obo:IAO_0000115 ?definition .} " +
                " %s }" +
                "}", definitionfilters.toString(), labelfilters.toString());
        multiKeyQuery.append(condition);
        return multiKeyQuery.toString();
    }

    /**
     * A general method for query. Provide a Query instance and RDF model, return an iterator for the results
     * @param query: created from key(s) by QueryFactory
     * @param hpomodel: RDF model
     * @param loincCodeClass the loinc code class used to provide keys
     * @return an iterator of query results
     */
    public static List<HPO_Class_Found> query(Query query, Model hpomodel, LoincCodeClass loincCodeClass) {
        QueryExecution qexec = QueryExecutionFactory.create(query, hpomodel);
        Iterator<QuerySolution> results = qexec.execSelect();
        List<HPO_Class_Found> HPO_classes_found = new ArrayList<>();
        addFoundClasses(HPO_classes_found, results, loincCodeClass);
        if(HPO_classes_found.size() > 1)
            Collections.sort(HPO_classes_found);
            Collections.reverse(HPO_classes_found);
        return HPO_classes_found;
    }

    /**
     * A method to do manual query with provided keys (literally)
     */
    public static List<HPO_Class_Found> query_manual(List<String> keys,
                                                     LoincCodeClass loincCodeClass) {
        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            String looseQueryString = buildLooseQueryWithMultiKeys(keys);
            Query query = QueryFactory.create(looseQueryString);
            return query(query, model, loincCodeClass);
        }
    }

    /**
     * A method to automatically query the HPO
     * @param loincLongCommonName
     * @return
     */
    public static List<HPO_Class_Found> query_auto(String loincLongCommonName) {

        if(!modelCreated) {
            createHPOModel();
        }
        List<HPO_Class_Found> HPO_classes_found = new ArrayList<>();

        LoincCodeClass loincClass = LoincLongNameParser.parse(loincLongCommonName);
        Queue<String> keys_in_parameter = loincClass.keysInLoinParameter();
        Queue<String> keys_in_tissue = loincClass.keysInLoincTissue();
        Stack<String> keys_in_use = new Stack<>();


        //first loop through keys in loinc parameter, until it finds a good key that yields > 0 class
        Queue<String> keys_in_parameter_copy1 = new LinkedList<>(keys_in_parameter);
        while (!keys_in_parameter_copy1.isEmpty()) {
            logger.info("Enter loop1");
            String key = keys_in_parameter_copy1.remove();
            keys_in_use.clear();
            keys_in_use.push(key);
            logger.info("new key used for query: " + key);
            String standardQueryString = buildStandardQueryWithSingleKey(key);
            Query query = QueryFactory.create(standardQueryString);
            HPO_classes_found = query(query, model, loincClass);
            if (HPO_classes_found.size() != 0) break;
        }

        //loop through remaining keys in loinc parameter, until it reduces results to < 20
        boolean forceToContinue = false;
        while ((HPO_classes_found.size() > 20 || forceToContinue) && !keys_in_parameter_copy1.isEmpty()) {
            logger.info("Enter loop2");
            forceToContinue = false;
            String key = keys_in_parameter_copy1.remove();
            keys_in_use.add(key);
            logger.info("query with " + keys_in_use.size() + " keys");
            logger.info("new key used for query: " + key);
            String stardardQueryString = buildStandardQueryWithMultiKeys(keys_in_use);
            Query query = QueryFactory.create(stardardQueryString);
            HPO_classes_found = query(query, model, loincClass);
            if (HPO_classes_found.size() == 0) {
                if (!keys_in_parameter_copy1.isEmpty()) {//if adding a key suddenly fails the query, remove the last key and continue
                    keys_in_use.pop();
                    forceToContinue = true;
                    logger.info("A bad key is detected and poped" + key);
                } else { //if cannot continue, go back one step
                    keys_in_use.pop();
                    logger.info("Going back one step");
                    HPO_classes_found = query(QueryFactory.create(buildStandardQueryWithMultiKeys(keys_in_use)), model, loincClass);
                }
            }
        }

        if (HPO_classes_found.size() > 20 && !keys_in_tissue.isEmpty()) { //use tissue to search
            logger.info("start to use tissue for query");
            keys_in_use.add(new Synset().getSynset((List<String>)keys_in_tissue).convertToRe());
            String standardQueryString = buildStandardQueryWithMultiKeys(keys_in_use);
            logger.info("query with " + keys_in_use.size() + " keys");
            for (String key : keys_in_use) {
                logger.info(key);
            }
            logger.info("query string: \n" + standardQueryString);
            Query query = QueryFactory.create(standardQueryString);
            HPO_classes_found = query(query, model, loincClass);
            if (HPO_classes_found.size() == 0) {
                keys_in_use.pop();
                HPO_classes_found = query(QueryFactory.create(buildStandardQueryWithMultiKeys(keys_in_use)), model, loincClass);
            }
        }  //if there are still more than 20 classes, then we can do nothing


        //if no HPO classes are found using standard query, then lower threshold (remove modifier)
        if (HPO_classes_found.size() == 0) {

            Queue<String> keys_in_parameter_copy2 = new LinkedList<>(keys_in_parameter);
            while (!keys_in_parameter_copy2.isEmpty()) {
                logger.info("enter loop to find the first key that can HPO classes with loose method. ");
                String key = keys_in_parameter_copy2.remove();
                keys_in_use.clear();
                keys_in_use.push(key);
                logger.info("key used: " + key);
                String standardQueryString = buildLooseQueryWithSingleKey(key);
                Query query = QueryFactory.create(standardQueryString);
                HPO_classes_found = query(query, model, loincClass);
                if (HPO_classes_found.size() != 0) break;
            }

            //if a single key finds too many classes, add additional keys to reduce the number
            forceToContinue = false;
            while ((HPO_classes_found.size() > 20 || forceToContinue) && !keys_in_parameter_copy2.isEmpty()) {
                logger.info("Enter loop to use additional keys to reduce the number of classes.");
                forceToContinue = false;
                String key = keys_in_parameter_copy2.remove();
                keys_in_use.add(key);
                logger.info("query with " + keys_in_use.size() + " keys");
                logger.info("new key used for query: " + key);
                String looseQueryString = buildLooseQueryWithMultiKeys(keys_in_use);
                Query query = QueryFactory.create(looseQueryString);
                HPO_classes_found = query(query, model, loincClass);
                if (HPO_classes_found.size() == 0) {
                    if (!keys_in_parameter_copy2.isEmpty()) {//if adding a key suddenly fails the query, remove the last key and continue
                        keys_in_use.pop();
                        forceToContinue = true;
                        logger.info("A bad key is detected and poped: " + key);
                    } else { //if cannot continue, go back one step
                        keys_in_use.pop();
                        logger.info("Going back one step");
                        HPO_classes_found = query(QueryFactory.create(buildLooseQueryWithMultiKeys(keys_in_use)), model, loincClass);
                    }
                }
            }

            //if there are still too many classes, use tissue for search
            if (HPO_classes_found.size() > 20 && !keys_in_tissue.isEmpty()) { //use tissue to search
                logger.info("start to use tissue for query");
                keys_in_use.add(new Synset().getSynset((List<String>)keys_in_tissue).convertToRe());
                String looseQueryString = buildLooseQueryWithMultiKeys(keys_in_use);
                logger.info("query with " + keys_in_use.size() + " keys");
                for (String key : keys_in_use) {
                    logger.info(key);
                }
                logger.info("query string: \n" + looseQueryString);
                Query query = QueryFactory.create(looseQueryString);
                HPO_classes_found = query(query, model, loincClass);
                if (HPO_classes_found.size() == 0) { //if adding tissue reduces HPO classes to 0, then go back
                    keys_in_use.pop();
                    HPO_classes_found = query(QueryFactory.create(buildLooseQueryWithMultiKeys(keys_in_use)), model, loincClass);
                }
            }  //if there are still more than 20 classes, then we can do nothing
        }


        //if no HPO classes can be found after looping through all keys in loinc parameter, then
        //none of the keys are good; such HPO classes either do not exist, or the user should try
        //some synomes.
        if (HPO_classes_found.size() == 0) {
            System.out.println("NO HPO terms are found. Try some synonymes.");
        }
        System.out.println(HPO_classes_found.size() + " HPO classes are found!");

        return HPO_classes_found;
    }



    /**
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
        String queryString = buildStandardQueryWithSingleKey(parameters[0]);
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, hpomodel);
        Iterator<QuerySolution> results = qexec.execSelect();
        int count = 0;
        count = addFoundClasses(HPO_classes_found, results, loincClass);
        System.out.println(count + " results are found!");

        if (count < 3) { //loose criteria
            HPO_classes_found = new ArrayList<>();
            queryString = buildLooseQueryWithSingleKey(parameters[0]);
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
     **/


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








    public static void main(String[] args) {
        //String hpo = SparqlQuery.class.getResource("/hp.owl").getPath(); //need '/' to get a resource file
        //System.out.println("hpo path: " + hpo);
        //Model model = getOntologyModel(hpo);


        if(!modelCreated) {
        createHPOModel();
        }

        Path path = Paths.get(SparqlQuery.class.getResource("/loinc_most_frequent.csv").getFile());
        String newline;
        try (InputStream input = Files.newInputStream(path)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter writer = new PrintWriter("loinc-hpo-mapping.tsv");
            reader.readLine();
            writer.write("Loinc Long Common Name\tScore\tCandidate HPO Class URI\tCandidate HPO Class Label\tCandidate HPO Class Definition\n");
            while ((newline = reader.readLine()) != null) {
                //List<HPO_Class_Found> hpo_clsses_found = SparqlQuery.query(newline, model);
                List<HPO_Class_Found> hpo_clsses_found = query_auto(newline);
                if (hpo_clsses_found.size() > 0 && hpo_clsses_found.size() < 20) {
                    for (HPO_Class_Found HPO_class : hpo_clsses_found) {
                        StringBuilder outContent = new StringBuilder();
                        outContent.append(newline);
                        outContent.append("\t");
                        outContent.append(HPO_class.getScore() + "\t");
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
                    writer.write(newline + "\tToo many (" + hpo_clsses_found.size() + ") classes are found. List top 15 only\n");
                    int count = 0;
                    for (HPO_Class_Found HPO_class : hpo_clsses_found) {
                        count++;
                        if(count > 15) break;
                        StringBuilder outContent = new StringBuilder();
                        outContent.append(newline);
                        outContent.append("\t");
                        outContent.append(HPO_class.getScore() + "\t");
                        outContent.append(HPO_class.getId() + "\t");
                        outContent.append(HPO_class.getLabel() + "\t");
                        if (HPO_class.getDefinition() != null)  {
                            outContent.append(HPO_class.getDefinition());
                        }
                        outContent.append("\n");
                        writer.write(outContent.toString());
                    }
                }


            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

/**
        if(!modelCreated) {
            createHPOModel();
        }
        String key = "testosterone";
        String looseQueryString = SparqlQuery.buildLooseQueryWithSingleKey(key);
        String standardQueryString = SparqlQuery.buildStandardQueryWithSingleKey(key);
        Query looseQuery = QueryFactory.create(looseQueryString);
        Query standardQuery = QueryFactory.create(standardQueryString);
        List<HPO_Class_Found> results_loose = query(looseQuery, model, null);
        List<HPO_Class_Found> results_standard = query(standardQuery, model, null);
        System.out.println(results_loose.size() + " HPO terms are found!");
        System.out.println(results_standard.size()+ " HPO terms are found!");
 **/
    }
}

package org.monarchinitiative.loinc2hpo.io;

import javafx.concurrent.Task;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.LocationMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class OntologyModelBuilderForJena extends Task<Model> {

    private String pathToOntology;
    private String pathToOntologyInRDF;
    private static final Logger logger = LogManager.getLogger();

    public OntologyModelBuilderForJena(String pathToOntology) {
        this.pathToOntology = pathToOntology;
    }

    public OntologyModelBuilderForJena(String pathToOntology, String pathToOntologyInRDF) {
        this.pathToOntology = pathToOntology;
        this.pathToOntologyInRDF = pathToOntologyInRDF;
    }


    @Override
    protected Model call() throws Exception {

        logger.trace("enter function to build ontology model for Sparql query");
        //explicitely state that the model is Jena RDF model
        //org.apache.jena.rdf.model.Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        LocationMapper locationMapper = new LocationMapper(this.pathToOntology);
        logger.trace("location map is set");
        FileManager.get().addLocatorClassLoader(OntologyModelBuilderForJena.class.getClassLoader());
        logger.trace("locator is set");
        if (pathToOntologyInRDF != null && new File(pathToOntologyInRDF).exists()) {
            logger.trace("use rdf to create model");
            org.apache.jena.rdf.model.Model model = FileManager.get().loadModel(pathToOntologyInRDF);
            return model;
        }
        org.apache.jena.rdf.model.Model model = ModelFactory.createDefaultModel();
        try {
            logger.trace("start reading hpo");
            InputStream in = FileManager.get().open(pathToOntology);
            try {
                model.read(in, null);
                logger.trace("read ontology for Jena correctly from: " + this.pathToOntology);
            } catch (Exception e) {
                logger.error("cannot read in data to model");
            }
        } catch (JenaException je) {
            logger.error("cannot open hpo.owl");
        }
        logger.trace("exit function to build ontology model for Sparql query.");
        return model;
    }


}

package org.monarchinitiative.loinc2hpo.io;

import javafx.concurrent.Task;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.InputStream;

public class OntologyModelBuilderForJena extends Task<Model> {

    private static final Logger logger = LogManager.getLogger();

    private String pathToOntology;

    /**
     * Load .owl to Jena model
     * @param pathToOntology
     */
    public OntologyModelBuilderForJena(String pathToOntology) {
        this.pathToOntology = pathToOntology;
    }


    @Override
    protected Model call() throws Exception {

        logger.trace("enter function to build ontology model for Sparql query");
        logger.trace("PATH= is set "+pathToOntology);
        /**
        if (pathToOntologyInRDF != null && new File(pathToOntologyInRDF).exists()) {
            logger.trace("use rdf to create model");
            org.apache.jena.rdf.model.Model model = FileManager.get().loadModel(pathToOntologyInRDF);
            return model;
        }
         **/
        //explicitely state that the model is Jena RDF model
        org.apache.jena.rdf.model.Model jenaModel = ModelFactory.createDefaultModel();
        //We can also create a more advanced model, but it is slower and not necessary since we are not editing the model
        //OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        logger.info("default ontology model created");
        try {
            logger.trace("start reading hpo");
            InputStream in = FileManager.get().open(pathToOntology);
            jenaModel.read(in, null);
        } catch (JenaException je) {
            logger.error("cannot open hpo.owl");
        }
        logger.trace("exit function to build ontology model for Sparql query.");
        return jenaModel;
    }

}

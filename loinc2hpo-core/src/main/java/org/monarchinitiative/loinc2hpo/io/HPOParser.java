package org.monarchinitiative.loinc2hpo.io;

import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import com.github.phenomics.ontolib.io.obo.hpo.HpoOboParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Use <a href="https://github.com/Phenomics/ontolib">ontolib</a> to parse the HPO OBO file.
 */
public class HPOParser {
    private static final Logger LOGGER = LogManager.getLogger();

    private HpoOntology hpo=null;

    public HPOParser(String absolutePathToHpoObo) {
        LOGGER.trace(String.format("Initializing HPO obo parser for %s",absolutePathToHpoObo));
        parse(absolutePathToHpoObo);
    }

    private void parse(String path) {
        File f=new File(path);
        if (!f.exists()) {
            LOGGER.error(String.format("Unable to find HPO file at %s",path));
            return;
        }
        final HpoOboParser parser = new HpoOboParser(f);
        try {
            this.hpo = parser.parse();
        } catch(IOException e) {
            LOGGER.error(String.format("I/O error with HPO file at %s",path));
            LOGGER.error(e,e);
        }
    }
    /** @return an initiliazed HPO ontology or null in case of errors. */
    public HpoOntology getHPO() { return this.hpo; }

}

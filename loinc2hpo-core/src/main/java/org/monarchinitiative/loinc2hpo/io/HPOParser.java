package org.monarchinitiative.loinc2hpo.io;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;

import java.io.File;
import java.io.IOException;

/**
 * Use <a href="https://github.com/Phenomics/ontolib">ontolib</a> to parse the HPO OBO file.
 * This class overlaps with HpoOntologyParser
 */
@Deprecated
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
        final HpOboParser parser = new HpOboParser(f);
        try {
            this.hpo = parser.parse();
        } catch(PhenolException e) {
            LOGGER.error(String.format("I/O error with HPO file at %s",path));
            LOGGER.error(e,e);
        }
    }
    /** @return an initiliazed HPO ontology or null in case of errors. */
    public HpoOntology getHPO() { return this.hpo; }

}

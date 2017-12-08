package org.monarchinitiative.loinc2hpo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.command.Command;
import org.monarchinitiative.loinc2hpo.io.Commandline;

/**
 * Prototype code for transforming LOINC encoded lab results to HPO
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 *  * @author <a href="mailto:aaron.zhang@jax.org">Aaron Zhang</a>
 * @version 0.0.1
 */
public class Loinc2Hpo {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String args[]) {
        Commandline clp = new Commandline(args);
        Command command = clp.getCommand();
        logger.trace(String.format("running command %s",command));
        command.execute();
    }


}

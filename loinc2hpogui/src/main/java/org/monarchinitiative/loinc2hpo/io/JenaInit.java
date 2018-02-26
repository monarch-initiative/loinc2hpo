package org.monarchinitiative.loinc2hpo.io;

import org.apache.jena.query.ARQ;
import org.apache.jena.riot.RIOT;
import org.apache.jena.system.JenaSubsystemLifecycle;

/**
 * This is a critical class to for Jena to work from Jar.
 * The class specifies how Jena should be initialized.
 * @author Aaron Zhang
 */

public class JenaInit implements JenaSubsystemLifecycle {
    @Override
    public void start() {

        RIOT.init(); //required to load owl
        ARQ.init(); //required to do query
    }

    @Override
    public void stop() {

    }
}

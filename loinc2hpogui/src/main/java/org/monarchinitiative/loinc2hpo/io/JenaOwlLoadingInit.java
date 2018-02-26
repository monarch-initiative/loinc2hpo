package org.monarchinitiative.loinc2hpo.io;

import org.apache.jena.query.ARQ;
import org.apache.jena.riot.RIOT;
import org.apache.jena.system.JenaSubsystemLifecycle;
import org.apache.jena.util.FileManager;

public class JenaOwlLoadingInit implements JenaSubsystemLifecycle {
    @Override
    public void start() {

        RIOT.init();
        ARQ.init();
    }

    @Override
    public void stop() {

    }
}

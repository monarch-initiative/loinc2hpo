package org.monarchinitiative.loinc2hpo.gui.application;

import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.monarchinitiative.loinc2hpo.gui.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class contains definitions of Spring beans used in HGMD app. Created by Daniel Danis on 7/16/17.
 */
@Configuration
@Import({ScreensConfig.class})
@PropertySource("file:${propertiesPath}")
public class ApplicationConfig {

    private static final Logger log = LogManager.getLogger(ApplicationConfig.class);

    @Autowired
    private Environment env;

    /**
     * The manager of resources required to run the app.
     */
    @Bean
    public HRMDResourceManager hrmdResourceManager() {
        File settings = new File(Main.HRMD_RESOURCES);
        try {
            return new HRMDResourceManager(settings);
        } catch (IOException e) {
            log.error(String.format("Error creating HRMDResourceManager from file %s", settings.getPath()));
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
    }

    @Bean
    public Ontology ontology() throws IOException, OBOParserException {
        OBOParser parser = new OBOParser(new OBOParserFileInput(hrmdResourceManager().getResources().getHpOBOPath()), OBOParser
                .PARSE_DEFINITIONS);
        String result = parser.doParse();
        log.info(String.format("HPO file parse result: %s", result));
        TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser
                .getDate());
        return Ontology.create(termContainer);
    }

  /*
    @Bean
    public CompletenessValidator completnessValidator() {
        return new CompletenessValidator();
    }

    @Bean
    public PubMedValidator pubMedValidator() {
        return new PubMedValidator();
    }
    */
}

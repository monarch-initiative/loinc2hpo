package org.monarchinitiative.loinc2hpo.io;

import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.formats.hpo.HpoTermRelation;
import com.github.phenomics.ontolib.ontology.data.Ontology;
import com.google.common.collect.ImmutableMap;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class HpoOntologyParserTest {

    /** The subontology of the HPO with all the phenotypic abnormality terms. */
    private static HpoOntology phenotypeSubOntology =null;
    /** The subontology of the HPO with all the inheritance terms. */
    private static Ontology<HpoTerm, HpoTermRelation> inheritanceSubontology=null;

    private static  ImmutableMap<String,HpoTerm> termmap=null;

    @BeforeClass
    public static void setup() throws IOException {
        ClassLoader classLoader = HpoOntologyParserTest.class.getClassLoader();
        String hpoPath = classLoader.getResource("hp.obo").getFile();
        HpoOntologyParser parser = new HpoOntologyParser(hpoPath);
        parser.parseOntology();
        termmap=parser.getTermMap();

    }


    @Test
    public void testTermMapNotNull() {
        assertNotNull(termmap);
    }



}

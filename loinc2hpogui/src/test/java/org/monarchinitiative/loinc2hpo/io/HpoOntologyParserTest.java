package org.monarchinitiative.loinc2hpo.io;

import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.phenol.ontology.data.Ontology;

@Ignore
public class HpoOntologyParserTest {
    final String hpo_owl = "/Users/zhangx/git/human-phenotype-ontology/hp.owl";
    final String hpo_owl_edit = "/Users/zhangx/git/human-phenotype-ontology/src/ontology/hp-edit.owl";
    final String hpo_obo = "/Users/zhangx/git/human-phenotype-ontology/src/ontology/hp.obo";
    @Test (expected = Test.None.class)
    public void parseObo() throws Exception {
        HpoOntologyParser hpoOntologyParser = new HpoOntologyParser(hpo_obo);
        hpoOntologyParser.parseOntology();
    }

    @Test (expected = Test.None.class)
    public void parseOwl() throws Exception {
        HpoOntologyParser hpoOntologyParser = new HpoOntologyParser(hpo_owl);
        hpoOntologyParser.parseOntology();
    }

    @Test (expected = Test.None.class)
    public void parseOwlEdit() throws Exception {
        HpoOntologyParser hpoOntologyParser = new HpoOntologyParser(hpo_owl_edit);
        hpoOntologyParser.parseOntology();
    }

    @Test
    public void getOntology() throws Exception {
        HpoOntologyParser hpoOntologyParser = new HpoOntologyParser(hpo_owl_edit);
        hpoOntologyParser.parseOntology();
        Ontology hpoOntology = hpoOntologyParser.getOntology();
        System.out.println(hpoOntology.getTerms().size());
    }

    @Test
    public void getTermMap() throws Exception {
        HpoOntologyParser hpoOntologyParser = new HpoOntologyParser(hpo_owl_edit);
        hpoOntologyParser.parseOntology();
        System.out.println(hpoOntologyParser.getTermMap().get("Hyperglycemia").getId());
    }

    @Test
    public void getTermMap2() throws Exception {
    }

}
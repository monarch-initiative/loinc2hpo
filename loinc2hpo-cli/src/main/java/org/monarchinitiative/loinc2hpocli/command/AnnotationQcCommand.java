package org.monarchinitiative.loinc2hpocli.command;

import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotationEntry;
import org.monarchinitiative.loinc2hpocore.io.Loinc2HpoAnnotationParser;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

@CommandLine.Command(name = "annotation-qc", aliases = {"qc"},
        mixinStandardHelpOptions = true,
        description = "quality assess the loinc2hpo-annotation.tsv file")
public class AnnotationQcCommand implements Runnable{

    @CommandLine.Option(names = {"-a", "-annot"},
            description = "Path to the loinc2hpo-annotation.tsv file",
            required = true)
    private String annotPath;
    @CommandLine.Option(names = {"--hpo"},
            description = "path to the hp.json file",
            required = true)
    private String hpJsonPath;


    @Override
    public void run() {
        System.out.println(annotPath);
        Loinc2HpoAnnotationParser parser = new Loinc2HpoAnnotationParser(annotPath);
        List<Loinc2HpoAnnotationEntry> entries = parser.getEntries();
        System.out.println("[INFO] Got " + entries.size() + " LOINC annotations.");
        Ontology ontology = OntologyLoader.loadOntology(new File(hpJsonPath));
        System.out.println("[INFO] Got " + ontology.countNonObsoleteTerms() + " HPO terms.");
        checkValidityOfHpoTerms(entries, ontology);

    }


    private void checkValidityOfHpoTerms(List<Loinc2HpoAnnotationEntry> entries, Ontology ontology) {
        for (var entry : entries) {
            TermId tid = TermId.of(entry.getHpoTermId());
            if (! ontology.containsTerm(tid)) {
                System.err.println("[ERROR] HPO does not contain TermId " + tid.getValue());
            } else if ( ! ontology.getPrimaryTermId(tid).equals(tid)) {
                System.err.println("[ERROR] Obsolete TermId (" + tid.getValue() + ") used instead of " +
                        ontology.getPrimaryTermId(tid) + ".");
            }
        }
    }


}

package org.monarchinitiative.loinc2hpocli.command;

import org.monarchinitiative.loinc2hpocli.analysis.LoincCoreTableParser;
import org.monarchinitiative.loinc2hpocli.html.Loinc2HpoTemplate;
import org.monarchinitiative.loinc2hpocli.html.LoincVisualizable;
import org.monarchinitiative.loinc2hpocore.model.LoincAnnotation;
import org.monarchinitiative.loinc2hpocore.model.LoincScale;
import org.monarchinitiative.loinc2hpocore.model.Outcome;
import org.monarchinitiative.loinc2hpocore.parser.Loinc2HpoAnnotationParser;
import org.monarchinitiative.loinc2hpocore.model.LoincEntry;
import org.monarchinitiative.loinc2hpocore.model.LoincId;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@CommandLine.Command(name = "html",
        mixinStandardHelpOptions = true,
        description = "Output HTML file with annotations")
public class HtmlOutputCommand implements Runnable {


    @CommandLine.Option(names = {"-a", "--loinc"},
            description = "Path to the loinc2hpo-annotation.tsv file",
            required = true)
    private String loincAnnotationPath;

    /**
     * Path to /home/peter/data/loinc/LoincTableCore.csv
     */
    @CommandLine.Option(names = {"-t", "--table"},
            required = true,
            description = "path to LoincTableCore.csv")
    private String coreTable;

    @CommandLine.Option(names = {"--hpo"},
            description = "path to the hp.json file",
            required = true)
    private String hpJsonPath;

    public HtmlOutputCommand(){
    }

    @Override
    public void run() {
        Ontology ontology = OntologyLoader.loadOntology(new File(this.hpJsonPath));
        LoincCoreTableParser lctParser = new LoincCoreTableParser(this.coreTable);
        Map<LoincId, LoincEntry> entryMap = lctParser.getEntryMap();
        Loinc2HpoAnnotationParser parser = new Loinc2HpoAnnotationParser(this.loincAnnotationPath);
        Map<LoincId, LoincAnnotation> annotMap = parser. loincToHpoAnnotationMap();
        System.out.println("[INFO] Got " + annotMap.size() + " LOINC annotations.");
        List<LoincVisualizable> rows = new ArrayList<>();
        for (var entry : annotMap.entrySet()) {
            LoincId loincId = entry.getKey();
            LoincAnnotation loincAnnotation = entry.getValue();
            LoincEntry loincEntry = entryMap.get(loincId);
            if (loincEntry == null) {
                System.err.printf("[ERROR] Could not find loinc entry for %s.\n", loincId.toString());
                continue;
            }
            LoincId id = loincEntry.getLoincId();
            String component = loincEntry.getComponent();
            String longName = loincEntry.getLongName();

            for (var annot : loincAnnotation.allAnnotations()) {
                Outcome outcome = annot.getOutcome();
                TermId tid = annot.getHpoTermId();
                String termlabel = ontology.getTermLabel(tid).orElse("n/a");
                LoincScale scale = annot.getLoincScale();
                String biocuration = annot.getBiocuration();
                LoincVisualizable row = new LoincVisualizable(id,component,longName,outcome,tid,termlabel,scale,biocuration);
                rows.add(row);
            }


        }
        try {
            Loinc2HpoTemplate template = new Loinc2HpoTemplate(rows);
            template.outputFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

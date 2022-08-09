package org.monarchinitiative.loinc2hpocli.command;

import org.monarchinitiative.loinc2hpocore.annotation.Loinc2HpoAnnotation;
import org.monarchinitiative.loinc2hpocore.annotation.LoincAnnotation;
import org.monarchinitiative.loinc2hpocore.annotation.LoincScale;
import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;
import org.monarchinitiative.loinc2hpocore.io.Loinc2HpoAnnotationParser;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import picocli.CommandLine;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private final Set<ShortCode> quantitativeCodes = Set.of(ShortCode.H, ShortCode.N, ShortCode.L);
    private final Set<ShortCode> ordinalCodes = Set.of(ShortCode.POS, ShortCode.NEG);
    private final Set<ShortCode> nominalCodes = Set.of(ShortCode.NOM);



    @Override
    public void run() {
        System.out.println(annotPath);
        Loinc2HpoAnnotationParser parser = new Loinc2HpoAnnotationParser(annotPath);
        List<Loinc2HpoAnnotation> entries = parser.getEntries();
        System.out.println("[INFO] Got " + entries.size() + " LOINC annotations.");
        Ontology ontology = OntologyLoader.loadOntology(new File(hpJsonPath));
        System.out.println("[INFO] Got " + ontology.countNonObsoleteTerms() + " HPO terms in hp.json.");
        checkValidityOfHpoTerms(entries, ontology);

       Map<LoincId, LoincAnnotation> mymap = parser.loincToHpoAnnotationMap();
       System.out.println("[INFO] " + mymap.size() + " annotated LOINC terms");
       checkValidityOfLoincAnnotations(mymap);

    }


    private void checkValidityOfHpoTerms(List<Loinc2HpoAnnotation> entries, Ontology ontology) {
        int good = 0;
        for (var entry : entries) {
            TermId tid = entry.getHpoTermId();
            if (! ontology.containsTerm(tid)) {
                System.err.println("[ERROR] HPO does not contain TermId " + tid.getValue());
            } else if ( ! ontology.getPrimaryTermId(tid).equals(tid)) {
                System.err.println("[ERROR] Obsolete TermId (" + tid.getValue() + ") used instead of " +
                        ontology.getPrimaryTermId(tid) + ".");
            } else {
                good++;
            }
        }
        System.out.printf("[INFO] %d well-formed HPO terms used in LOINC annotations\n", good);
    }

    private void checkValidityOfLoincAnnotations(Map<LoincId, LoincAnnotation> annotmap) {
        int malformed = 0;
        for (var annot : annotmap.values()) {
            if (annot.scale().equals(LoincScale.QUANTITATIVE)) {
                malformed += checkInvalidQuantitative(annot);
            } else if (annot.scale().equals(LoincScale.ORDINAL)) {
                malformed += checkInvalidOrdinal(annot);
            } else if (annot.scale().equals(LoincScale.NOMINAL)) {
                malformed += checkInvalidNominal(annot);
            } else {
                // should never haqppen
                throw new Loinc2HpoRuntimeException("Unrecognized loinc scale");
            }
        }
        System.out.printf("[INFO] %s well formed, %d malformed annotations.\n",
                annotmap.size() - malformed, malformed);
    }

    /**
     * @param annot
     * @return 1 if malformed, 0 if OK
     */
    private int checkInvalidQuantitative(LoincAnnotation annot) {
        List<Loinc2HpoAnnotation> annots = annot.allAnnotations();
        for (var l2h : annots) {
            Outcome out = l2h.getOutcome();
            if (! quantitativeCodes.contains(out.getCode())) {
                System.err.printf("[ERROR] Malformed QN outcome code (\"%s\"): %s\n", out.getCode(), annot);
                return 1;
            }
        }
        return 0;//ok if we get here
    }

    /**
     * @param annot
     * @return 1 if malformed, 0 if OK
     */
    private int checkInvalidOrdinal(LoincAnnotation annot) {
        List<Loinc2HpoAnnotation> annots = annot.allAnnotations();
        for (var l2h : annots) {
            Outcome out = l2h.getOutcome();
            if (! ordinalCodes.contains(out.getCode())) {
                System.err.printf("[ERROR] Malformed ordinal outcome code (\"%s\"): %s\n", out.getCode(), annot);
                return 1;
            }
        }
        return 0;//ok if we get here
    }

    /**
     * @param annot
     * @return 1 if malformed, 0 if OK
     */
    private int checkInvalidNominal(LoincAnnotation annot) {
        List<Loinc2HpoAnnotation> annots = annot.allAnnotations();
        for (var l2h : annots) {
            Outcome out = l2h.getOutcome();
            if (! nominalCodes.contains(out.getCode())) {
                System.err.printf("[ERROR] Malformed nominal outcome code: %s\n", annot);
                return 1;
            }
        }
        return 0;//ok if we get here
    }


}

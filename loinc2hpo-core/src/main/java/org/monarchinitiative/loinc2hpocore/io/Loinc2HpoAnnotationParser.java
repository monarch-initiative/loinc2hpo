package org.monarchinitiative.loinc2hpocore.io;


import org.monarchinitiative.loinc2hpocore.annotationmodel.*;
import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is responsible for parsing the {@code loinc2hpo-annotations.tsv} file that is available
 * at https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation
 * @author Peter Robinson, Aaron Zhang
 */
public class Loinc2HpoAnnotationParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(Loinc2HpoAnnotationParser.class);

    private final static Set<ShortCode> quantitativeCodeSet = Set.of(ShortCode.L, ShortCode.N, ShortCode.H);
    private final static Set<ShortCode> ordinalCodeSet = Set.of(ShortCode.ABSENT, ShortCode.PRESENT);
    private final static Set<ShortCode> nominalCodeSet = Set.of(ShortCode.NOM);

    private final List<Loinc2HpoAnnotation> entries;

    public Loinc2HpoAnnotationParser(String path) {
        entries = importAnnotations(path);
    }

    private List<Loinc2HpoAnnotation> importAnnotations(String path) {
        List<Loinc2HpoAnnotation> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))){
            String line = reader.readLine(); // header
            if (!line.equals(String.join("\t", Loinc2HpoAnnotation.headerFields))){
                throw new Loinc2HpoRuntimeException("Annotation header does not match expected fields!");
            }
            while ((line = reader.readLine()) != null){
                entries.add(Loinc2HpoAnnotation.fromAnnotationLine(line));
            }
        } catch (IOException e) {
            throw new Loinc2HpoRuntimeException(e.getMessage());
        }
        return entries;
    }

    public List<Loinc2HpoAnnotation> getEntries() {
        return entries;
    }

    public Map<TermId, LoincAnnotation> loincToHpoAnnotationMap() {
        Map<TermId, List<Loinc2HpoAnnotation>> result = entries.stream()
                .collect(Collectors.groupingBy(Loinc2HpoAnnotation::getLoincId,
                        Collectors.mapping(Function.identity(),
                                Collectors.toList())));
        Map<TermId, LoincAnnotation> outcomesMap = new HashMap<>();
        for (var e : result.entrySet()) {
            TermId loincId = e.getKey();
            List<Loinc2HpoAnnotation> outcomes = e.getValue();
            LoincAnnotation lannot = getLoincAnnotation(outcomes);
            outcomesMap.put(loincId, lannot);
        }
        return outcomesMap;
    }

    private LoincAnnotation getLoincAnnotation(List<Loinc2HpoAnnotation> outcomes) {
        int n = outcomes.size();
        // map with all of the outcomes for the current LOINC test
        Map<Outcome, Loinc2HpoAnnotation> outcomeMap = outcomes.stream()
                .collect(Collectors.toMap(Loinc2HpoAnnotation::getOutcome, Function.identity()));
        // are there three distinct quantitative outcomes, i.e., L/N/H, i.e.,valid Qn?
        if (n == 3 && outcomeMap.keySet().stream().filter(Outcome::isQuantitative).count() == 3) {
            return new QuantitativeLoincAnnotation(outcomeMap.get(Outcome.LOW()),
                    outcomeMap.get(Outcome.NORMAL()),
                    outcomeMap.get(Outcome.HIGH()));
            // are there two distinct ordinal outcomes, i.e., Absent/Present, i.e.,valid Ord?
        } else if (n == 2 && outcomeMap.keySet().stream().filter(Outcome::isOrdinal).count() == 2) {
            return new OrdinalHpoAnnotation(outcomeMap.get(Outcome.ABSENT()), outcomeMap.get(Outcome.PRESENT()));
        }  // are all outcomes nominal
        else if (n == outcomeMap.keySet().stream().filter(Outcome::isNominal).count()) {
            return new NominalLoincAnnotation(outcomeMap);
        } else {
            for (var oc : outcomes) {
                System.err.println("[ERROR] " + oc);
            }
            throw new Loinc2HpoRuntimeException("Malformed outcomes");
        }

    }


    public static List<Loinc2HpoAnnotation> load(String path) {
        Loinc2HpoAnnotationParser parser = new Loinc2HpoAnnotationParser(path);
        return parser.getEntries();
    }
}

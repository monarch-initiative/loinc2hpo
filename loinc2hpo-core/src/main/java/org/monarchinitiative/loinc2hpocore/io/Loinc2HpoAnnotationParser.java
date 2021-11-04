package org.monarchinitiative.loinc2hpocore.io;


import org.monarchinitiative.loinc2hpocore.annotation.*;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
                String msg = String.format("Annotation header (%s) does not match expected fields (%s)",
                        line, String.join("\t", Loinc2HpoAnnotation.headerFields));
                throw new Loinc2HpoRuntimeException(msg);
            }
            while ((line = reader.readLine()) != null){
                entries.add(Loinc2HpoAnnotation.fromAnnotationLine(line));
            }
        } catch (IOException e) {
            throw new Loinc2HpoRuntimeException(e.getMessage());
        }
        return entries;
    }


    public static void exportToTsv(List<Loinc2HpoAnnotation> annotations, String path) throws IOException {
        File outfile = new File(path);
        LOGGER.info("Writing annotation data to {}", outfile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
        String header = String.join("\t", Loinc2HpoAnnotation.headerFields);
        bw.write(header + "\n");
        Collections.sort(annotations);
        for (var ann : annotations) {
            bw.write(ann.toTsv() + "\n");
        }
        bw.close();
    }



    public List<Loinc2HpoAnnotation> getEntries() {
        return entries;
    }

    public Map<LoincId, LoincAnnotation> loincToHpoAnnotationMap() {
        Map<LoincId, List<Loinc2HpoAnnotation>> result = entries.stream()
                .collect(Collectors.groupingBy(Loinc2HpoAnnotation::getLoincId,
                        Collectors.mapping(Function.identity(),
                                Collectors.toList())));
        Map<LoincId, LoincAnnotation> outcomesMap = new HashMap<>();
        for (var e : result.entrySet()) {
            LoincId loincId = e.getKey();
            List<Loinc2HpoAnnotation> outcomes = e.getValue();
            LoincAnnotation lannot = Loinc2HpoAnnotation.outcomes2LoincAnnotation(outcomes);
            outcomesMap.put(loincId, lannot);
        }
        return outcomesMap;
    }




    public static List<Loinc2HpoAnnotation> load(String path) {
        Loinc2HpoAnnotationParser parser = new Loinc2HpoAnnotationParser(path);
        return parser.getEntries();
    }
}

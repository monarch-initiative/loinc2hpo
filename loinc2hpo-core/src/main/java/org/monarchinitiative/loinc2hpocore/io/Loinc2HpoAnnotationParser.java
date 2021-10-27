package org.monarchinitiative.loinc2hpocore.io;

import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotationCsvEntry;
import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for parsing the {@code loinc2hpo-annotations.tsv} file that is available
 * at https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation
 * @author Peter Robinson, Aaron Zhang
 */
public class Loinc2HpoAnnotationParser {

    private final List<Loinc2HpoAnnotationCsvEntry> entries;

    private final List<String> expectedFields = List.of("loincId","loincScale","system",
            "code","hpoTermId","isNegated","createdOn","createdBy","lastEditedOn","lastEditedBy",
            "version","isFinalized","comment");

    public Loinc2HpoAnnotationParser(String path) {
        entries = importAnnotations(path);
    }

    private List<Loinc2HpoAnnotationCsvEntry> importAnnotations(String path) {
        List<Loinc2HpoAnnotationCsvEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))){
            String line = reader.readLine(); // header
            if (!line.equals(String.join("\t", expectedFields))){
                throw new RuntimeException("header does not match expected!");
            }
            while ((line = reader.readLine()) != null){
                Loinc2HpoAnnotationCsvEntry newEntry =
                        Loinc2HpoAnnotationCsvEntry.fromTsvLine(line);
                entries.add(newEntry);
            }
        } catch (IOException e) {
            throw new Loinc2HpoRuntimeException(e.getMessage());
        }
        return entries;
    }

    public List<Loinc2HpoAnnotationCsvEntry> getEntries() {
        return entries;
    }

    public static List<Loinc2HpoAnnotationCsvEntry> load(String path) {
        Loinc2HpoAnnotationParser parser = new Loinc2HpoAnnotationParser(path);
        return parser.getEntries();
    }

}

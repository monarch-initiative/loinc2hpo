package org.monarchinitiative.loinc2hpogui.loinc;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.loinc.Loinc2HpoAnnotationCsvEntry;
import org.monarchinitiative.loinc2hpocore.loinc.Loinc2HpoAnnotationModel;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class Loinc2HpoAnnotationModelTest {

    @Test
    public void from_csv() throws Exception {

        String annotationPath = this.getClass().getClassLoader().getResource("annotations.tsv").getPath();
        Map<LoincId, Loinc2HpoAnnotationModel> annotationModelMap = Loinc2HpoAnnotationModel.from_csv(annotationPath);
        assertTrue(annotationModelMap.size() > 100);

    }

    @Test
    public void to_csv_entries() throws Exception {
        String annotationPath = this.getClass().getClassLoader().getResource("annotations.tsv").getPath();
        Map<LoincId, Loinc2HpoAnnotationModel> annotationModelMap = Loinc2HpoAnnotationModel.from_csv(annotationPath);

        List<String> lines_to_write = annotationModelMap.values().stream()
                .map(Loinc2HpoAnnotationModel::to_csv_entries)
                .flatMap(Collection::stream)
                .map(Loinc2HpoAnnotationCsvEntry::toString)
                .map(String::trim)
                .collect(Collectors.toList());

        BufferedReader reader = new BufferedReader(new FileReader(annotationPath));

        List<String> lines_deserialized = new ArrayList<>();
        String line = reader.readLine();//skip header
        while ((line = reader.readLine()) != null){
            lines_deserialized.add(line.trim());
        }

        assertEquals(lines_to_write.size(), lines_deserialized.size());
        assertArrayEquals(lines_to_write.toArray(), lines_deserialized.toArray());
    }

}
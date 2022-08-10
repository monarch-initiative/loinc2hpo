package org.monarchinitiative.loinc2hpocli.analysis;

import org.monarchinitiative.loinc2hpocore.model.LoincEntry;
import org.monarchinitiative.loinc2hpocore.model.LoincId;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoincCoreTableParser {

    private final Map<LoincId, LoincEntry> entryMap;
    public LoincCoreTableParser(String coreTable) {
        File loincTableCore = new File(coreTable);
        if (! loincTableCore.isFile()) {
            throw new PhenolRuntimeException("Could not find Loinc Core Table at " + coreTable);
        }
        entryMap = new HashMap<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(coreTable))) {
            br.readLine(); // header
            while ((line = br.readLine()) != null) {
                LoincEntry entry = LoincEntry.fromQuotedCsvLine(line);
                entryMap.put(entry.getLoincId(), entry);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.printf("Got %d LOINC Entries.\n", entryMap.size());
    }

    public Map<LoincId, LoincEntry> getEntryMap() {
        return Map.copyOf(entryMap);
    }


}

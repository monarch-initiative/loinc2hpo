package org.monarchinitiative.loinc2hpocli.command;


import org.monarchinitiative.loinc2hpocore.model.LoincScale;
import org.monarchinitiative.loinc2hpocore.parser.LoincTableCoreParser;
import org.monarchinitiative.loinc2hpocore.model.LoincEntry;
import picocli.CommandLine;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@CommandLine.Command(name = "stats", aliases = {"S"},
        mixinStandardHelpOptions = true,
        description = "Q/C and stats for the LoincTableCore.csv file")
public class LoincTableCoreStatsCommand implements Runnable{
    @CommandLine.Option(names = {"-l", "--loinc"},
            description = "Path to the loinc2hpo-annotation.tsv file",
            required = true)
    private String loincTableCorePath;

    public LoincTableCoreStatsCommand(){
    }

    @Override
    public void run() {
        LoincTableCoreParser parser = new LoincTableCoreParser(loincTableCorePath);
        var loincEntryMap = parser.getLoincEntries();
        System.out.printf("%d well formed entries.\n", loincEntryMap.size());
        System.out.printf("%d non-(Qn,Ord,Nom) scale entries (removed).\n", parser.getInvalidScale());
        System.out.printf("%d malformed entries.\n", parser.getMalformed());
        Map<LoincScale, List<LoincEntry>> entriesByScale = loincEntryMap.values().stream()
                .collect(groupingBy(LoincEntry::getScale));
        int total = loincEntryMap.size();
        for (var e : entriesByScale.entrySet()) {
            int size = e.getValue().size();
            String perc = String.format("%.1f%%", 100.0*size/total);
            System.out.printf("%s: %d entries (%s)\n", e.getKey(), size, perc);
        }
    }
}

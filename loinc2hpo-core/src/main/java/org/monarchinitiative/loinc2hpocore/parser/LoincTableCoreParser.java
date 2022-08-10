package org.monarchinitiative.loinc2hpocore.parser;

import org.monarchinitiative.loinc2hpocore.model.Loinc2HpoRuntimeException;
import org.monarchinitiative.loinc2hpocore.model.LoincEntry;
import org.monarchinitiative.loinc2hpocore.model.LoincId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Parse LoincTableCore.csv file to obtain {@link LoincEntry} objects
 */
public class LoincTableCoreParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoincTableCoreParser.class);

    private final Map<LoincId, LoincEntry> loincEntries;

    private final int malformed;
    /** Count of LOINC codes with scales other than Qn, Ord, and Nom, which we skip. */
    private final int invalidScale;

    public LoincTableCoreParser(String pathToLoincCoreTable) {
        Map<LoincId, LoincEntry> tmp = new HashMap<>();
        int count_malformed = 0;
        int count_invalid_scale = 0;
        int n=0;
        try (BufferedReader br = new BufferedReader(new FileReader(pathToLoincCoreTable))){
            String line;
            String header=br.readLine();
            if (! header.equals(LoincEntry.header)) {
                LOGGER.error(String.format("Malformed header line (%s) in Loinc File %s",header,pathToLoincCoreTable));
                throw new Loinc2HpoRuntimeException("Malformed LoincTableCore.tsv header line");
            }
            while ((line=br.readLine())!=null) {
                n++;
                try {
                    LoincEntry entry = LoincEntry.fromQuotedCsvLine(line);
                    if (entry.getScale().validForLoinc2Hpo()) {
                        tmp.put(entry.getLoincId(), entry);
                    } else {
                        count_invalid_scale++;
                    }
                } catch (Loinc2HpoRuntimeException e) {
                    LOGGER.error(e.getMessage());
                    LOGGER.error("Line {}: {}", n, line);
                    count_malformed++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info(tmp.size() + " LOINC entries were created");
        malformed = count_malformed;
        invalidScale = count_invalid_scale;
        if (count_malformed>0) {
            LOGGER.error(count_malformed + " LOINC entries are malformed");
        }
        this.loincEntries = Map.copyOf(tmp); // immutable copy
    }

    public Map<LoincId, LoincEntry> getLoincEntries() {
        return loincEntries;
    }

    public int getMalformed() {
        return malformed;
    }

    public int getInvalidScale() {
        return invalidScale;
    }

    public static Map<LoincId, LoincEntry> load(String pathToLoincCoreTable) {
        LoincTableCoreParser parser = new LoincTableCoreParser(pathToLoincCoreTable);
        return parser.getLoincEntries();
    }

}

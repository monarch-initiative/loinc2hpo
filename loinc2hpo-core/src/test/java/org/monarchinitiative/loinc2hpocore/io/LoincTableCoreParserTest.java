package org.monarchinitiative.loinc2hpocore.io;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;
import org.monarchinitiative.loinc2hpocore.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoincTableCoreParserTest {

    private static String LoincTableCoreTinyPath;

    @BeforeAll
    public static void init() {
        URL url = LoincTableCoreParserTest.class.getClassLoader().getResource("LoincTableCoreTiny.csv");
        if (url==null) {
            throw new Loinc2HpoRuntimeException("Could not get path to \"LoincTableCoreTiny.csv\"");
        }
        LoincTableCoreTinyPath = url.getPath();
    }

    /**
     * We expected 28 entries
     */
    @Test
    void testParser() {
        Map<LoincId, LoincEntry> entryMap = LoincTableCoreParser.load(LoincTableCoreTinyPath);
        assertEquals(28, entryMap.size());
    }

}

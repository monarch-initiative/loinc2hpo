package org.monarchinitiative.loinc2hpocore.loinc;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.annotationmodel.LoincScale;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


/*
"LOINC_NUM","COMPONENT","PROPERTY","TIME_ASPCT","SYSTEM","SCALE_TYP","METHOD_TYP","CLASS","CLASSTYPE","LONG_COMMON_NAME","SHORTNAME","EXTERNAL_COPYRIGHT_NOTICE","STATUS","VersionFirstReleased","VersionLastChanged"

 */
public class LoincEntryTest {

    @Test
    void testConstruction() {
        String [] entryFields = {"10000-8","R wave duration.lead AVR","Time","Pt","Heart","Qn","EKG","EKG.MEAS","2","R wave duration in lead AVR","R wave dur L-AVR","","ACTIVE","1.0i","2.48"};
        List<String> quotedEntryFields = Arrays.stream(entryFields).map(w -> String.format("\"%s\"", w)).collect(Collectors.toList());
        String entryLine1 = String.join(",", quotedEntryFields);
        LoincEntry entry = LoincEntry.fromQuotedCsvLine(entryLine1);
        assertEquals("R wave duration.lead AVR", entry.getComponent());
        assertEquals("Pt", entry.getTimeAspect());
        LoincId id = new LoincId("10000-8");
        assertEquals(id, entry.getLoincId());
        assertEquals("EKG",entry.getMethod());
    }

    @Test
    void testDichlorophenoxyacetate() {
        String [] fields = { "9806-1","2,4-Dichlorophenoxyacetate","MCnc","Pt","Urine","Qn","","DRUG/TOX","1","2,4-Dichlorophenoxyacetate [Mass/volume] in Urine","2,4D Ur-mCnc","","ACTIVE","1.0i","2.42"};
        List<String> quotedEntryFields = Arrays.stream(fields).map(w -> String.format("\"%s\"", w)).collect(Collectors.toList());
        String line = String.join(",",quotedEntryFields);
        LoincEntry entry = LoincEntry.fromQuotedCsvLine(line);
        LoincId loincId = new LoincId("9806-1");
        assertEquals(loincId, entry.getLoincId());
        assertEquals("2,4-Dichlorophenoxyacetate", entry.getComponent());
        assertEquals("MCnc", entry.getProperty());
        assertEquals("Pt", entry.getTimeAspect());
        assertEquals("Urine", entry.getSystem());
        assertEquals(LoincScale.QUANTITATIVE, entry.getScale());
        assertEquals("", entry.getMethod());
        assertEquals("2,4-Dichlorophenoxyacetate [Mass/volume] in Urine", entry.getLongName());
        LoincLongName lln = entry.getLoincLongName();
        assertEquals("Urine", lln.getLoincTissue());
        assertEquals("2,4-Dichlorophenoxyacetate", lln.getLoincParameter());
    }


}

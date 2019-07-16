package org.monarchinitiative.loinc2hpo.loinc;

import com.google.common.collect.ImmutableMap;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore
public class LoincPanelComponentTest {

    private static final String loincCoreTable = "/Users/zhangx/Downloads/LOINC_2/LoincTableCore.csv";
    public static final String loincPanels = "/Users/zhangx/Downloads/LOINC_2/Accessory/PanelsAndForms/LOINC_263_PanelsAndForms_Panels.csv";
    private static ImmutableMap<LoincId, LoincEntry> loincEntryMap;
    private static LoincPanelComponent component;

    @BeforeClass
    public static void setUp() throws Exception {
        loincEntryMap = LoincEntry.getLoincEntryMap(loincCoreTable);
        LoincId testLoinc = new LoincId("2160-0");
        LoincPanelComponent.setLoincEntryMap(loincEntryMap);
        component = new LoincPanelComponent(testLoinc, PanelComponentConditionality.R);
    }

    @Test
    public void getLoincEntry() throws Exception {
        assertNotNull(component.getLoincEntry());
    }

    @Test
    public void getLoincId() throws Exception {
        assertEquals("2160-0", component.getLoincEntry().getLOINC_Number().toString());
    }

    @Test
    public void getTestingConditionality() throws Exception {
        assertEquals(PanelComponentConditionality.R, component.getTestingConditionality());
        component.setTestingConditionality(PanelComponentConditionality.O);
        assertEquals(PanelComponentConditionality.O, component.getTestingConditionality());
    }

    @Test
    public void getMappingConditionality() throws Exception {
        assertNull(component.getConditionalityForParentMapping());
        component.setConditionalityForParentMapping(PanelComponentConditionality.R);
        assertEquals(PanelComponentConditionality.R, component.getConditionalityForParentMapping());
    }

}
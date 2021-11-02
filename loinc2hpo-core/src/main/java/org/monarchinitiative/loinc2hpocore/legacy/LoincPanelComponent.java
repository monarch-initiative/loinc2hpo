package org.monarchinitiative.loinc2hpocore.legacy;

import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.loinc2hpocore.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

public class LoincPanelComponent {

    private static ImmutableMap<LoincId, LoincEntry> loincEntryMap; // All LOINC entries
    private LoincId loincId; // LOINC id for this component
    private PanelComponentConditionality testingConditionality; // Is this LOINC test required to complete the order of the panel
    private PanelComponentConditionality conditionalityForParentMapping; // Is this LOINC required to map the panel to a HPO term
    private boolean interpretableInHPO; // Is this LOINC interpretable in HPO terms? e.g. systolic and diastolic BP can be interpreted in HPO terms; while a time point in a series of glucose tolerance test cannot be interpreted as an isolated data point

    public LoincPanelComponent(LoincId loincId, PanelComponentConditionality conditionalityForParentTesting) {
        this.loincId = loincId;
        this.testingConditionality = conditionalityForParentTesting;
    }

    public static void setLoincEntryMap(ImmutableMap<LoincId, LoincEntry> immutableLoincEntryMap) {
        loincEntryMap = immutableLoincEntryMap;
    }

    public LoincEntry getLoincEntry() {
        return loincEntryMap.get(loincId);
    }

//    public LoincId getLoincId() {
//        return loincId;
//    }

    public void setLoincId(LoincId loincId) {
        this.loincId = loincId;
    }

    public PanelComponentConditionality getTestingConditionality() {
        return testingConditionality;
    }

    public void setTestingConditionality(PanelComponentConditionality testingConditionality) {
        this.testingConditionality = testingConditionality;
    }

    public PanelComponentConditionality getConditionalityForParentMapping() {
        return conditionalityForParentMapping;
    }

    public void setConditionalityForParentMapping(PanelComponentConditionality conditionalityForParentMapping) {
        this.conditionalityForParentMapping = conditionalityForParentMapping;
    }

    public boolean isInterpretableInHPO() {
        return this.interpretableInHPO;
    }

    public void setInterpretableInHPO(boolean interpretableInHPO) {
        this.interpretableInHPO = interpretableInHPO;
    }
}

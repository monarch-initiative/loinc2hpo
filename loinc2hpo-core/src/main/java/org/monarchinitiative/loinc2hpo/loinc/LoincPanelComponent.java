package org.monarchinitiative.loinc2hpo.loinc;

import com.google.common.collect.ImmutableMap;

public class LoincPanelComponent {

    private ImmutableMap<LoincId, LoincEntry> loincEntryMap;
    private LoincId loincId;
    private PanelComponentConditionality testingConditionality;
    private PanelComponentConditionality mappingConditionality;



    public LoincPanelComponent(LoincId loincId, PanelComponentConditionality conditionality, ImmutableMap<LoincId, LoincEntry> loincEntryMap) {
        this.loincId = loincId;
        this.testingConditionality = conditionality;
        this.loincEntryMap = loincEntryMap;
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

    public PanelComponentConditionality getMappingConditionality() {
        return mappingConditionality;
    }

    public void setMappingConditionality(PanelComponentConditionality mappingConditionality) {
        this.mappingConditionality = mappingConditionality;
    }
}

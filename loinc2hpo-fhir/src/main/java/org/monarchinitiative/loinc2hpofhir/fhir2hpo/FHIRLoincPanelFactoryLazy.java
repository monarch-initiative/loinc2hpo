package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.FHIRLoincPanelConversionLogic.BloodPressurePanel;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.FHIRLoincPanelConversionLogic.GlasgowComaPanel;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

public class FHIRLoincPanelFactoryLazy implements FHIRLoincPanelFactory {
    /**
     * Implement the factory in a lazy manner:
     * check the requested panel LOINC id, then return a correct panel
     * As we implement more panels, this might become too laborious
     * @TODO: Is there a better strategy?
     * @param loincId
     * @return
     */
    @Override
    public FHIRLoincPanel createFhirLoincPanel(LoincId loincId) {
        switch (loincId.toString()) {
            case "35094-2":
                return new BloodPressurePanel(loincId);
            case "35088-4":
                return new GlasgowComaPanel(loincId);
            //... add more cases after implementation
            default:
                return null;
        }
    }

    @Override
    public FHIRLoincPanel createFhirLoincPanel(LoincId loincId, Patient patient) {
        FHIRLoincPanel panel = createFhirLoincPanel(loincId);
        if (panel != null) {
            panel.setSubject(patient);
        }
        return panel;
    }
}

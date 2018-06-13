package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.util.List;
import java.util.Set;

/**
 * This interface defines key functions to analyze LOINC observations that belong to panels.
 * The logic is as follows:
 *
 * 1) Regenstrief has released a LOINC PanelsAndForms excel file that contains the mapping between panels and their components. We rely on this file to check whether a LOINC observation belongs to a panel.
 * 2) We will build a separate annotation file that indicates if a panel can be mapped to HPO terms.
 * 3) If a panel can be mapped to a HPO term, we will use its conversion logic, which may need hard-coding, to map to a HPO term.
 */
public interface FHIRLoincPanels {

    /**
     * Return the subject of the panel
     * @return
     */
    Patient getSubject();

    /**
     * Return the subject id of the panel
     * @return
     */
    String getPatientId();

    /**
     * Return the LOINC code of the panel
     * @return
     */
    Set<LoincId> panelIds();

    /**
     * Add a component observation to the panel
     * @param observation
     */
    void addComponent(Observation observation);

    /**
     * Add a list of observations to the panel
     * @param observations
     */
    void addComponents(List<Observation> observations);

    /**
     * Add a list of observations belonging to the same panel
     * @param observationsOfPanel
     */
    void addComponentsOfSamePanel(List<Observation> observationsOfPanel);

    /**
     * Group a test to a panel belonging to the patient.
     * Note:
     * it is important to consider two situations-
     * 1) a test may belong to multiple panels
     * 2) a patient may have multiple instances of the same panel, e.g. LOINC 35094-2 blood pressure panel is ordered multiple times.
     * @param observation
     */
    void groupToPanel(Observation observation);

    /**
     * This function calls all panels to interpret to a HPO term with their own logic.
     */
    void interpret();

}

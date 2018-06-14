package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.util.List;
import java.util.Map;

/**
 * This interface defines a collection of FHIR resources belonging to a LOINC panel. For example, a class that implements this interface can be instantiated to represent LOINC 35094-2 Blood pressure panel. It is highly likely that we need to define concrete classes to present each panel in order to implement the ObservationAnalysis interface
 */
public interface FHIRLoincPanel extends ObservationAnalysis {

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
    LoincId panelId();

    /**
     * Return a list of the individual tests
     * @return
     */
    List<Observation> panelComponents();

    /**
     * Add a component observation to the panel
     * @param observation
     */
    void addComponent(Observation observation);

}

package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpocore.annotationmodel.Loinc2HpoAnnotation;
import org.monarchinitiative.loinc2hpocore.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.*;

/**
 * This interface defines a collection of FHIR resources belonging to a LOINC panel. For example, a class that implements this interface can be instantiated to represent LOINC 35094-2 Blood pressure panel. It is highly likely that we need to define concrete classes to present each panel in order to implement the ObservationAnalysis interface
 */
public interface FHIRLoincPanel extends ObservationAnalysis {

    Set<LoincId> loincIdSet = new HashSet<>();
    Map<LoincId, LoincEntry> loincEntryMap = new HashMap<>();
    Map<LoincId, Loinc2HpoAnnotation> loincAnnotationMap = new HashMap<>();

    static void initResources(Set<LoincId> loincIdSetX, Map<LoincId, LoincEntry> loincEntryMapX,
                              Map<LoincId, Loinc2HpoAnnotation> annotationMapX){
        loincIdSet.addAll(loincIdSetX);
        loincEntryMap.putAll(loincEntryMapX);
        loincAnnotationMap.putAll(annotationMapX);
    }

//    /**
//     * Create a new instance of FHIRLoincPanel
//     * @param loincId
//     * @return
//     */
//    FHIRLoincPanel instance(LoincId loincId);
//
//    /**
//     * Create a new instance of FHIRLoincPanel for specified subject
//     * @param loincId
//     * @param subject
//     * @return
//     */
//    FHIRLoincPanel instance(LoincId loincId, Patient subject);

    /**
     * Set the subject for the LOINC panel
     * @param patient
     */
    void setSubject(Patient patient);

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
     * @param loincId
     * @param observation
     */
    void addComponent(LoincId loincId, Observation observation);

    /**
     * Add all components to a panel
     * Use Map as argument as it will make future work easier
     * @param observations
     */
    void addComponents(Map<LoincId, Observation> observations);

}

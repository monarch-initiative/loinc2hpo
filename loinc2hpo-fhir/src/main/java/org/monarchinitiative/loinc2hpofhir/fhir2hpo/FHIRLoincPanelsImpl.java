package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpofhir.fhir2hpo.FHIRLoincPanelConversionLogic.BloodPressurePanel;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.*;

/**
 * This class manages all LOINC panels belonging to a patient. Note: a patient may have multiple instances of the same panel. e.g. Blood pressure panel, a patient may have many blood pressure measurements. It will be difficult to group panel componenets if they are not added as a group.
 */
public class FHIRLoincPanelsImpl implements FHIRLoincPanels {

    private final Patient subject;
    private String patientId;
    //
    private final HashMap<LoincId, List<FHIRLoincPanel>> loincPanels;

    public FHIRLoincPanelsImpl(Patient subject) {
        this.subject = subject;
        this.loincPanels = new HashMap<>();
    }

    private static Map<LoincId, Set<LoincId>> componentToParentMap = null;

    public static void setComponentToPanelMap(Map<LoincId, Set<LoincId>> componentToPanelMap) {
        componentToParentMap = componentToPanelMap;
    }

    @Override
    public Patient getSubject() {
        return this.subject;
    }

    @Override
    public String getPatientId() {
        if (this.subject == null) {
            return null;
        }
        return this.subject.getId();
    }

    @Override
    public Set<LoincId> panelIds() {
        return new HashSet<>(this.loincPanels.keySet());
    }

    @Override
    public void addComponent(Observation observation) {

    }

    @Override
    public void addComponents(LoincId panelLoinc, List<Observation> observations) {
        FHIRLoincPanel newPanel = new BloodPressurePanel(panelLoinc);
        loincPanels.putIfAbsent(panelLoinc, new ArrayList<>());
        loincPanels.get(panelLoinc).add(newPanel);
    }

    // Find the panel LOINC of a list of LOINC observations
//    private Set<LoincId> panelLoinc(List<Observation> observationList) {
//        List<String> loincStrings = new ArrayList<>();
//        observationList.stream().map(FhirObservationAnalyzer::getLoincIdOfObservation)
//                .filter(l -> l.size() == 1)  //each observation should only have one loinc id
//                .forEach(loincStrings::addAll);
//        Set<Set<LoincId>> panelSets = loincStrings.stream().map(loincString -> {
//            try {
//                return new LoincId(loincString);
//            } catch (MalformedLoincCodeException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }).map(loincid -> componentToParentMap.get(loincid))
//                .collect(Collectors.toSet());
//
//        //TODO: replace the set intersection with a library function call
//        Set<LoincId> target = null;
//        if (!panelSets.isEmpty()) {
//            Iterator<Set<LoincId>> itr = panelSets.iterator();
//            target = itr.next();
//            while (itr.hasNext()) {
//                Set<LoincId> next = itr.next();
//                for (LoincId loincId : next) {
//                    if (!target.contains(loincId)) {
//                        target.remove(loincId);
//                    }
//                }
//            }
//        }
//        return target;
//    }

    @Override
    public void addComponentsOfSamePanel(List<Observation> observationsOfPanel) {

    }

    @Override
    public void groupToPanel(Observation observation) {

    }

    @Override
    public void interpret() {

    }
}

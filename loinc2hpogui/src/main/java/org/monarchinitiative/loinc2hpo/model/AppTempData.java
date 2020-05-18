package org.monarchinitiative.loinc2hpo.model;

import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.Constants;
import org.monarchinitiative.loinc2hpo.loinc.*;

import java.util.*;

import javafx.scene.paint.Color;

/**
 * Prototype model for LOINC to HPO Biocuration process.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.1.2 (2017-12-12)
 */
public class AppTempData {
    private static final Logger logger = LogManager.getLogger();

    @Inject Settings settings;

    private String pathToHpGitRepo = null;

    private Map<String, String> tempStrings = new HashMap<>();//hpo terms before being used to create an annotation
    private Map<String, String> tempAdvancedAnnotation = new HashMap<>();//a advanced annotation before it is being added to record
    private LOINC2HpoAnnotationImpl currentAnnotation = null;
    //private boolean tempInversed= false;
    private boolean inversedBasicMode = false; //whether inverse is checked for basic mode
    private boolean inversedAdvancedMode = false; //whether inverse is checked for advanced mode

    //keep a record of Loinc lists that user searched or filtered so that user can switch back and forth
    //key: file name used for filtering; value: a list of Loinc entries
    private Map<String, List<LoincEntry>> filteredLoincListsMap = new LinkedHashMap<>();
    private LinkedList<List<LoincEntry>> filteredLoincLists = new LinkedList<>();
    private List<LoincEntry> currentLoincList;

    //session changes whenever one of the following functions are called:
    //
    private boolean sessionChanged = false;

    public boolean isSessionChanged() {
        return sessionChanged;
    }

    public void setSessionChanged(boolean sessionChanged) {
        this.sessionChanged = sessionChanged;
    }

    public void addFilteredList(String filename, List<LoincEntry> list) {
        if (filteredLoincListsMap.containsKey(filename)) { //update sequence in map and list
            filteredLoincListsMap.remove(filename);
            filteredLoincLists = new LinkedList<>();
            filteredLoincLists.addAll(filteredLoincListsMap.values());
        }
        filteredLoincListsMap.put(filename, list);
        filteredLoincLists.add(list);
        currentLoincList = list;
    }

    public List<LoincEntry> previousLoincList(){
        if (filteredLoincLists.isEmpty()) {
            return null;
        }
        int current_i = filteredLoincLists.indexOf(currentLoincList);
        if (current_i - 1 >= 0) {
            currentLoincList = filteredLoincLists.get(current_i - 1);
        } else {
            currentLoincList = filteredLoincLists.getLast();
        }
        return currentLoincList;
    }

    public List<LoincEntry> nextLoincList() {
        if (filteredLoincLists.isEmpty()) {
            return null;
        }
        int current_i = filteredLoincLists.indexOf(currentLoincList);
        if (current_i + 1 <= filteredLoincLists.size() - 1) {
            currentLoincList = filteredLoincLists.get(current_i + 1);
        } else {
            currentLoincList = filteredLoincLists.getFirst();
        }
        return currentLoincList;
    }

    public List<LoincEntry> getLoincList(String listFileName) {
        if (filteredLoincListsMap.containsKey(listFileName)) {
            currentLoincList = filteredLoincListsMap.get(listFileName);
            filteredLoincListsMap.remove(listFileName);//remove and add to keep order
            filteredLoincListsMap.put(listFileName, currentLoincList);
            filteredLoincLists.clear();
            filteredLoincLists.addAll(filteredLoincListsMap.values());
            return currentLoincList;
        } else {
            return null;
        }
    }

    public Map<String, List<LoincEntry>> getFilteredLoincListsMap() {
        return this.filteredLoincListsMap;
    }


    private LoincEntry loincUnderEditing = null;

    public LoincEntry getLoincUnderEditing() {
        return loincUnderEditing;
    }

    public void setLoincUnderEditing(LoincEntry loincUnderEditing) {
        this.loincUnderEditing = loincUnderEditing;
    }

    /**
     * The following section handles github labels for HPO
     * (used to suggest new hpo terms for some loinc codes)
     */
    private List<String> labels = new ArrayList<>();
    public boolean hasLabels() {
        return !labels.isEmpty();
    }

    public void setGithublabels(List<String> labels) {
        this.labels.addAll(labels);
    }

    public List<String> getGithublabels() {
        return this.labels;
    }

    public String getPathToHpGitRepo() {
        return pathToHpGitRepo;
    }

    public void setTempTerms(Map<String, String> temp) { this.tempStrings = temp; }
    public Map<String, String> getTempTerms() { return new HashMap<>(this.tempStrings); }
    public void setTempAdvancedAnnotation(Map<String, String> tempAdvancedAnnotation) { this.tempAdvancedAnnotation = tempAdvancedAnnotation;}
    public Map<String, String> getTempAdvancedAnnotation() {return new HashMap<>(this.tempAdvancedAnnotation);}

    public boolean isInversedBasicMode() {
        return inversedBasicMode;
    }

    public void setInversedBasicMode(boolean inversedBasicMode) {
        this.inversedBasicMode = inversedBasicMode;
    }

    public boolean isInversedAdvancedMode() {
        return inversedAdvancedMode;
    }

    public void setInversedAdvancedMode(boolean inversedAdvancedMode) {
        this.inversedAdvancedMode = inversedAdvancedMode;
    }

    public void setCurrentAnnotation(LOINC2HpoAnnotationImpl current) {this.currentAnnotation = current;}
    public LOINC2HpoAnnotationImpl getCurrentAnnotation() {
        return currentAnnotation;
    }

    private String fhirServer = Constants.HAPIFHIRTESTSERVER;//default fhir server
    public String getFhirServer() {
        return fhirServer;
    }

    public void setFhirServer(String fhirServer) {
        this.fhirServer = fhirServer;
    }

    private List<String> fhirServers = new ArrayList<>(Arrays.asList(this.fhirServer));
    public List<String> getFhirServers() {
        return fhirServers;
    }

    public void setFhirServers(List<String> fhirServers) {
        this.fhirServers = fhirServers;
    }

    public List<String> defaultColorList() {
        return Arrays.asList(
                Color.CYAN.toString(),
                Color.MAGENTA.toString(),
                Color.PINK.toString(),
                Color.LIGHTBLUE.toString(),
                Color.ORANGE.toString(),
                Color.CHOCOLATE.toString()
                );
    }




}

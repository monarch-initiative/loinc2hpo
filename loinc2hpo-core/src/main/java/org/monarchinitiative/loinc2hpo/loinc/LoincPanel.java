package org.monarchinitiative.loinc2hpo.loinc;

import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.exception.UnrecognizedLoincCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Use this class to represent a LOINC panel, which is a collection of LOINC tests there are typically ordered together.
 *
 */
public class LoincPanel {

    private static final Logger logger = LoggerFactory.getLogger(LoincPanel.class);

    private static ImmutableMap<LoincId, LoincEntry> loincEntryMap;
    private final LoincId panelLoincId;
    private Set<LoincPanelComponent> chidren;
    private boolean interpretableInHPO;

    public LoincPanel(LoincId loincId) {
        this.panelLoincId = loincId;
        this.chidren = new LinkedHashSet<>();
    }

    public static void setLoincEntryMap(ImmutableMap<LoincId, LoincEntry> loincEntryMapX){
        loincEntryMap = loincEntryMapX;
    }

//    public LoincId getPanelLoincId() {
//        return panelLoincId;
//    }

    public LoincEntry getPanelLoincEntry() {
        return loincEntryMap.get(this.panelLoincId);
    }

    public void addChild(LoincPanelComponent child) {
//        for (LoincPanelComponent component : this.chidren) {
//            if (component.getLoincEntry().getLOINC_Number().equals(child.getLoincEntry().getLOINC_Number())) {
//                logger.error("attempting to add a component that already exists: " + child.getLoincEntry().getLOINC_Number().toString());
//                return;
//            }
//        }
        this.chidren.add(child);
    }

    public boolean componentExists(LoincId loincId) {
        if (this.chidren.size() == 0) {
            return false;
        }
        return this.chidren.stream()
                .map(c -> c.getLoincEntry().getLOINC_Number())
                .filter(loinc -> loinc.equals(loincId))
                .collect(Collectors.toList()).size() != 0;
    }

    public Set<LoincPanelComponent> getChidren() {
        return new HashSet<>(chidren);
    }

    public boolean isInterpretableInHPO() {
        return interpretableInHPO;
    }

    public void setInterpretableInHPO(boolean interpretableInHPO) {
        this.interpretableInHPO = interpretableInHPO;
    }

    public Set<LoincPanelComponent> getChildrenRequiredForMapping() {
        return this.chidren.stream()
                .filter(c -> c.getConditionalityForParentMapping() == PanelComponentConditionality.R) //only return required ones
                .collect(Collectors.toSet());
    }

    //change the mapping conditionality for one component of the panel
    public void setChildMappingConditionality(LoincId child, PanelComponentConditionality conditionality) {
        for (LoincPanelComponent component : this.chidren) {
            if (component.getLoincEntry().getLOINC_Number().equals(child)) {
                component.setConditionalityForParentMapping(conditionality);
                //System.out.println("change component: " + component.getLoincEntry().getLOINC_Number().toString());
            }
        }
    }

    public static Map<LoincId, LoincPanel> getPanels(String path, ImmutableMap<LoincId, LoincEntry> loincEntryMapX) throws IOException, MalformedLoincCodeException, UnrecognizedLoincCodeException {
        loincEntryMap = loincEntryMapX;
        Map<LoincId, LoincPanel> loincPanelMap = new HashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(path));
        String header = reader.readLine();
        int numElements = header.split("\\*\\*\\*\\*").length;
        //System.out.println("no. of elements per line: " + numElements);
        String line;
        String[] elements;
        int count = 0;
        int count_malformed = 0;
        Set<LoincId> invalidPanelId = new LinkedHashSet<>();
        Set<LoincId> invalidChildId = new LinkedHashSet<>();
        while ((line = reader.readLine()) != null) {
            while (line.split("\\*\\*\\*\\*").length < 31) {
                line = line.trim() + reader.readLine().trim();
            }
            count++;
            elements = line.split("\\*\\*\\*\\*");
            if (elements.length != numElements) {
                count_malformed++;
                logger.error("line index: " + count);
                logger.error(line);
                logger.error("line elements: " + elements.length);
            } else {
                LoincId panelLoinc = new LoincId(elements[1]);
    if (!loincEntryMap.containsKey(panelLoinc)) { //skip unrecognized LOINC code: UPDATE LOINC table version!
        //System.out.println("invalid loinc: " + panelLoinc.toString());
        invalidPanelId.add(panelLoinc);
        continue;
    }
                loincPanelMap.putIfAbsent(panelLoinc, new LoincPanel(panelLoinc));
                LoincId childLoinc = new LoincId(elements[5]);
    if (!loincEntryMap.containsKey(childLoinc)) { //skip unrecognized LOINC code: UPDATE LOINC table version!
        //System.out.println("invalid child Loinc: " + childLoinc.toString());
        invalidChildId.add(childLoinc);
        continue;
    }
                PanelComponentConditionality testingConditionality;
                if (!elements[8].trim().equals("NA")){
                    testingConditionality = PanelComponentConditionality.of(elements[8]);
                } else {
                    testingConditionality = PanelComponentConditionality.U;
                }
                LoincPanelComponent child = new LoincPanelComponent(childLoinc, testingConditionality);

                if (!loincPanelMap.get(panelLoinc).componentExists(childLoinc) && !childLoinc.equals(panelLoinc)) {
                    loincPanelMap.get(panelLoinc).addChild(child);
                }
            }
        }
        if (count_malformed != 0) {
            logger.error("no. malformed lines: " + count_malformed);
        }
        if (!invalidPanelId.isEmpty()) {
            logger.error("no. unrecognized panel Loinc from loinc entry map: " + invalidPanelId.size());
            invalidPanelId.forEach(System.out::println);
            throw new UnrecognizedLoincCodeException();
        }
        if (!invalidChildId.isEmpty()) {
            logger.error("no. invalid child Loinc from loinc entry map: " + invalidChildId.size());
            invalidChildId.forEach(System.out::println);
            throw new UnrecognizedLoincCodeException();
        }

        reader.close();

        return loincPanelMap;
    }
}

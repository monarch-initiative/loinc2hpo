package org.monarchinitiative.loinc2hpocli.html;

import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

import java.util.List;

public class LoincVisualizable {

    private final String LoincId;
    private final String component;
    private final String longName ;
    private final List<AnnotVisualizable> annotations;

    public LoincVisualizable(LoincId id, String component, String longName, List<AnnotVisualizable> annots) {
        this.LoincId = id.toString();
        this.component = component;
        this.longName = longName;
        this.annotations = annots;
    }

    public String getLoincId() {
        return LoincId;
    }

    public String getLoincAnchor() {
        //https://loinc.org/600-7/
        String url = "https://loinc.org/" + getLoincId() + "/";
        return String.format("<a href=\"%s\" target=\"__blank\">%s (%s)</a>", url, getLongName(), getLoincId());

    }

    public String getComponent() {
        return component;
    }

    public String getLongName() {
        return longName;
    }

    public List<AnnotVisualizable> getAnnotations() {
        return annotations;
    }

    public String getNrow() {
        return String.valueOf(annotations.size());
    }
}

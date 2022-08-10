package org.monarchinitiative.loinc2hpocli.html;

import org.monarchinitiative.loinc2hpocore.model.LoincId;
import org.monarchinitiative.loinc2hpocore.model.LoincScale;
import org.monarchinitiative.loinc2hpocore.model.Outcome;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;

public class LoincVisualizable {

    private final String LoincId;
    private final String component;
    private final String longName ;
    private final String outcome;
    private final String  tid;
    private final String  termlabel;
    private final String  loincscale;
    private final String  biocuration;

    public LoincVisualizable(LoincId id, String component, String longName, Outcome outcome, TermId tid, String termlabel, LoincScale loincscale, String biocuration) {
        this.LoincId = id.toString();
        this.component = component;
        this.longName = longName;
        this.outcome = outcome.getOutcome();
        this.tid = tid.getValue();
        this.termlabel = termlabel;
        this.loincscale = loincscale.shortName();
        this.biocuration = biocuration;
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

    public String getOutcome() {
        return outcome;
    }

    public String getTid() {
        return tid;
    }

    public String getTermlabel() {
        return termlabel;
    }

    public String getHpoAnchor() {
        String url = "https://hpo.jax.org/app/browse/term/" +getTid();
        return String.format("<a href=\"%s\" target=\"__blank\">%s (%s)</a>", url, getTermlabel(), getTid());
    }

    public String getLoincscale() {
        return loincscale;
    }

    public String getBiocuration() {
        return biocuration;
    }


}

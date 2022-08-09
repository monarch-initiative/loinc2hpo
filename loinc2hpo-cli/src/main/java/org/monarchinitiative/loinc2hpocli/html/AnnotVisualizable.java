package org.monarchinitiative.loinc2hpocli.html;

import org.monarchinitiative.loinc2hpocore.annotation.LoincScale;
import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class AnnotVisualizable {

    private final String outcome;
    private final String  tid;
    private final String  termlabel;
    private final String  loincscale;
    private final String  biocuration;


    public AnnotVisualizable(Outcome outcome, TermId tid, String termlabel, LoincScale loincscale, String biocuration) {
        this.outcome = outcome.getOutcome();
        this.tid = tid.getValue();
        this.termlabel = termlabel;
        this.loincscale = loincscale.shortName();
        this.biocuration = biocuration;
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

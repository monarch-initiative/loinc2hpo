package org.monarchinitiative.loinc2hpo.testresult;

import org.monarchinitiative.phenol.formats.hpo.HpoTerm;

import java.util.HashSet;
import java.util.Set;

public class AbnormalitySynonetImpl implements AbnormalitySynonet {

    private Set<HpoTerm> termSet;

    public AbnormalitySynonetImpl() {
        this.termSet = new HashSet<>();
    }

    @Override
    public Set<HpoTerm> getSet() {
        return new HashSet<>(this.termSet);
    }

    @Override
    public boolean has(HpoTerm term) {
        return this.termSet.contains(term);
    }

    @Override
    public void add(HpoTerm hpoTerm) {
        this.termSet.add(hpoTerm);
    }
}

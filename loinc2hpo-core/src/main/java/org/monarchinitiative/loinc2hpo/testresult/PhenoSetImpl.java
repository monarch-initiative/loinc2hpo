package org.monarchinitiative.loinc2hpo.testresult;

import org.jgrapht.alg.util.UnionFind;
import org.monarchinitiative.phenol.formats.hpo.HpoTerm;

import java.util.HashSet;
import java.util.Set;

public class PhenoSetImpl implements PhenoSet {

    private Set<HpoTerm> termSet; //tracks how hpo terms that are used in a time line. Note that it is not a complete set of a phenoset terms
    private UnionFind<HpoTerm> hpoTermUnionFind;

    public PhenoSetImpl(UnionFind<HpoTerm> hpoTermUnionFind) {

        this.termSet = new HashSet<>();
        this.hpoTermUnionFind = hpoTermUnionFind;
    }

    @Override
    public Set<HpoTerm> getSet() {
        return new HashSet<>(this.termSet);
    }

    @Override
    public boolean has(HpoTerm term) {
        if (termSet.isEmpty()) {
            return false;
        } else {
            return this.hpoTermUnionFind.inSameSet(term, termSet.iterator().next());
        }

    }

    @Override
    public void add(HpoTerm hpoTerm) {
        this.termSet.add(hpoTerm);
    }
}

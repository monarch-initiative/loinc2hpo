package org.monarchinitiative.loinc2hpofhir.phenotypemodel;

import org.jgrapht.alg.util.UnionFind;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.HashSet;
import java.util.Set;

public class PhenoSetImpl implements PhenoSet {

    private final Set<TermId> termSet; //tracks how hpo terms that are used in a time line. Note that it is not a complete set of a phenoset terms
    private final UnionFind<TermId> hpoTermUnionFind;

    public PhenoSetImpl(UnionFind<TermId> hpoTermUnionFind) {

        this.termSet = new HashSet<>();
        this.hpoTermUnionFind = hpoTermUnionFind;
    }

    @Override
    public Set<TermId> getSet() {
        return new HashSet<>(this.termSet);
    }

    @Override
    public boolean sameSet(TermId term) {
        if (termSet.isEmpty()) {
            return false;
        } else {
            return this.hpoTermUnionFind.inSameSet(term, termSet.iterator().next());
        }

    }

    @Override
    public boolean hasOccurred(TermId term) {
        return !this.termSet.isEmpty() && this.termSet.contains(term);
    }

    @Override
    public void add(TermId hpoTerm) {
        this.termSet.add(hpoTerm);
    }
}

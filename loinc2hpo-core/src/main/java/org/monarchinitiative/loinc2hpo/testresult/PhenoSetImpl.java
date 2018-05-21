package org.monarchinitiative.loinc2hpo.testresult;

import org.jgrapht.alg.util.UnionFind;
import org.monarchinitiative.phenol.ontology.data.Term;


import java.util.HashSet;
import java.util.Set;

public class PhenoSetImpl implements PhenoSet {

    private Set<Term> termSet; //tracks how hpo terms that are used in a time line. Note that it is not a complete set of a phenoset terms
    private UnionFind<Term> hpoTermUnionFind;

    public PhenoSetImpl(UnionFind<Term> hpoTermUnionFind) {

        this.termSet = new HashSet<>();
        this.hpoTermUnionFind = hpoTermUnionFind;
    }

    @Override
    public Set<Term> getSet() {
        return new HashSet<>(this.termSet);
    }

    @Override
    public boolean sameSet(Term term) {
        if (termSet.isEmpty()) {
            return false;
        } else {
            return this.hpoTermUnionFind.inSameSet(term, termSet.iterator().next());
        }

    }

    @Override
    public boolean hasOccurred(Term term) {
        return !this.termSet.isEmpty() && this.termSet.contains(term);
    }

    @Override
    public void add(Term hpoTerm) {
        this.termSet.add(hpoTerm);
    }
}

package phenotypemodel;

import org.jgrapht.alg.util.UnionFind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.monarchinitiative.loinc2hpofhir.phenotypemodel.PhenoSet;
import org.monarchinitiative.loinc2hpofhir.phenotypemodel.PhenoSetImpl;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PhenoSetImplTest {

    private PhenoSet phenoSet;
    private Term hypocapnia = Term.of(TermId.of("HP:0000123"), "Hypocapnia");
    private Term hypercapnia = Term.of(TermId.of("HP:0000124"), "Hypercapnia");

    @BeforeEach
    public void setup() {

        UnionFind<TermId> unionFind = mock(UnionFind.class);
        phenoSet = new PhenoSetImpl(unionFind);
        when(unionFind.inSameSet(hypocapnia.getId(), hypercapnia.getId())).thenReturn(true);
    }
    @Test
    public void sameSet() {

        assertFalse(phenoSet.sameSet(hypocapnia.getId()));
        phenoSet.add(hypercapnia.getId());
        assertTrue(phenoSet.sameSet(hypocapnia.getId()));

    }

    @Test
    public void hasOccurred() {

        assertFalse(phenoSet.hasOccurred(hypocapnia.getId()));
        phenoSet.add(hypocapnia.getId());
        assertTrue(phenoSet.hasOccurred(hypocapnia.getId()));
        assertFalse(phenoSet.hasOccurred(hypercapnia.getId()));
        phenoSet.add(hypercapnia.getId());
        assertTrue(phenoSet.hasOccurred(hypercapnia.getId()));
    }


}
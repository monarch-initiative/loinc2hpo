package org.monarchinitiative.loinc2hpocore.phenotypemodel;

import org.jgrapht.alg.util.UnionFind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.monarchinitiative.loinc2hpocore.phenotypemodel.*;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


public class PhenoSetTimeLineImplTest {

    private PhenoSetTimeLine glucosetimeLine;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private UnionFind<TermId> hpoTermUnionFind;
    private Term hypoglycemia;
    private Term hyperglycemia;
    private Term parent;

    @BeforeEach
    public void init() throws Exception {

        hypoglycemia = Term.of(TermId.of("HP:000012"), "Hypoglycemia");
        hyperglycemia = Term.of(TermId.of("HP:0000013"), "Hyperglycemia");
        parent = Term.of(TermId.of("HP:0000015"), "Abnormal blood glucose " +
                "level");

        hpoTermUnionFind = mock(UnionFind.class);

        glucosetimeLine = new PhenoSetTimeLineImpl(new PhenoSetImpl(hpoTermUnionFind));

        //create a few phenotype components
        PhenotypeComponent c1 = new PhenotypeComponentImpl.Builder()
                .start(dateFormat.parse("2016-09-30 09:30:00"))
                .hpoTerm(hypoglycemia.getId())
                .isNegated(false)
                .build();

        PhenotypeComponent c2 = new PhenotypeComponentImpl.Builder()
                .start(dateFormat.parse("2017-09-30 09:30:00"))
                .hpoTerm(hypoglycemia.getId())
                .isNegated(false)
                .build();

        PhenotypeComponent c3 = new PhenotypeComponentImpl.Builder()
                .start(dateFormat.parse("2018-09-30 09:30:00"))
                .hpoTerm(hyperglycemia.getId())
                .isNegated(false)
                .build();

        PhenotypeComponent c4 = new PhenotypeComponentImpl.Builder()
                .start(dateFormat.parse("2019-09-30 09:30:00"))
                .hpoTerm(hyperglycemia.getId())
                .isNegated(false)
                .build();

        PhenotypeComponent c5 = new PhenotypeComponentImpl.Builder()
                .start(dateFormat.parse("2020-09-30 09:30:00"))
                .hpoTerm(parent.getId())
                .isNegated(true)
                .build();

        glucosetimeLine.insert(c3);
        glucosetimeLine.insert(c1);
        glucosetimeLine.insert(c2);
        glucosetimeLine.insert(c4);
    }

    @Test
    public void phenoset() {
        assertEquals(2, glucosetimeLine.phenoset().getSet().size());
    }

    @Test
    public void getTimeLine() {
        assertEquals(4, glucosetimeLine.getTimeLine().size());
    }

    @Test
    public void insert() throws Exception {
        //already tested during test setup
    }

    @Test
    public void current() throws Exception {
        assertEquals(hypoglycemia.getId(), glucosetimeLine.current(dateFormat.parse(
                "2016-12-30 10:33:33")).abnormality());
        assertEquals(hypoglycemia.getId(), glucosetimeLine.current(dateFormat.parse(
                "2017-12-30 10:33:33")).abnormality());
    }

    @Test
    public void persistDuring() throws Exception {
        Date date1 = dateFormat.parse("2017-06-20 10:33:33");
        Date date2 = dateFormat.parse("2017-06-30 10:33:33");
        Date date3 = dateFormat.parse("2017-10-30 10:33:33");
        assertNotNull(glucosetimeLine.persistDuring(date1, date2));
        assertNull(glucosetimeLine.persistDuring(date2, date3));
        assertEquals(hypoglycemia.getId(),
                glucosetimeLine.persistDuring(date1, date2).abnormality());
    }

    @Test
    public void occurredDuring() throws Exception {
        Date date0 = dateFormat.parse("2000-10-30 10:33:33");
        Date date1 = dateFormat.parse("2017-06-20 10:33:33");
        Date date2 = dateFormat.parse("2017-06-30 10:33:33");
        Date date3 = dateFormat.parse("2017-10-30 10:33:33");
        Date date4 = dateFormat.parse("2017-10-30 10:33:33");
        Date date5 = dateFormat.parse("2019-10-30 10:33:33");
        assertNotNull(glucosetimeLine.occurredDuring(date0, date5));
        assertEquals(4, glucosetimeLine.occurredDuring(date0, date5).size());
        assertEquals(1, glucosetimeLine.occurredDuring(date0, date1).size());
        assertEquals(2, glucosetimeLine.occurredDuring(date0, date3).size());
    }

}
package org.monarchinitiative.loinc2hpo.testresult;

import org.jgrapht.alg.util.UnionFind;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.SharedResourceCollection;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class PhenoSetTimeLineImplTest {

    private PhenoSetTimeLine glucosetimeLine;
    private static Map<String, Term> hpoTermMap;
    private static Map<TermId, Term> hpoTermMap2;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static UnionFind<TermId> hpoTermUnionFind;

    @BeforeAll
    public static void setup() throws Exception{
        ResourceCollection resourceCollection = SharedResourceCollection.resourceCollection;

        hpoTermMap = resourceCollection.hpoTermMapFromName();
        hpoTermMap2 = resourceCollection.hpoTermMap();
        Ontology hpo = resourceCollection.getHPO();
        Map<LoincId, LOINC2HpoAnnotationImpl> annotationMap = resourceCollection.annotationMap();

        hpoTermUnionFind = new PhenoSetUnionFind(hpo.getTermMap().values().stream().map(Term::getId).collect(Collectors.toSet()), annotationMap).getUnionFind();
    }

    @BeforeAll
    public void init() throws Exception {

        glucosetimeLine = new PhenoSetTimeLineImpl(new PhenoSetImpl(hpoTermUnionFind));

        //create a few phenotype components
        PhenotypeComponent c1 = new PhenotypeComponentImpl.Builder()
                .start(dateFormat.parse("2016-09-30 09:30:00"))
                .hpoTerm(hpoTermMap.get("Hyperglycemia").getId())
                .isNegated(false)
                .build();

        PhenotypeComponent c2 = new PhenotypeComponentImpl.Builder()
                .start(dateFormat.parse("2017-09-30 09:30:00"))
                .hpoTerm(hpoTermMap.get("Hypoglycemia").getId())
                .isNegated(false)
                .build();

        PhenotypeComponent c3 = new PhenotypeComponentImpl.Builder()
                .start(dateFormat.parse("2018-09-30 09:30:00"))
                .hpoTerm(hpoTermMap.get("Hyperglycemia").getId())
                .isNegated(false)
                .build();

        PhenotypeComponent c4 = new PhenotypeComponentImpl.Builder()
                .start(dateFormat.parse("2019-09-30 09:30:00"))
                .hpoTerm(hpoTermMap.get("Hypoglycemia").getId())
                .isNegated(false)
                .build();

        PhenotypeComponent c5 = new PhenotypeComponentImpl.Builder()
                .start(dateFormat.parse("2020-09-30 09:30:00"))
                .hpoTerm(hpoTermMap.get("Abnormality of blood glucose concentration").getId())
                .isNegated(true)
                .build();

        glucosetimeLine.insert(c3);
        glucosetimeLine.insert(c1);
        glucosetimeLine.insert(c2);
        glucosetimeLine.insert(c4);
    }

    @Test
    public void phenoset() throws Exception {
        assertEquals(2, glucosetimeLine.phenoset().getSet().size());
    }

    @Test
    public void getTimeLine() throws Exception {
        assertEquals(4, glucosetimeLine.getTimeLine().size());
    }

    @Test
    public void insert() throws Exception {
        //already tested during test setup
    }

    @Test
    public void current() throws Exception {
        assertEquals(hpoTermMap.get("Hyperglycemia").getId(), glucosetimeLine.current(dateFormat.parse("2016-12-30 10:33:33")).abnormality());
        assertEquals(hpoTermMap.get("Hypoglycemia").getId(), glucosetimeLine.current(dateFormat.parse("2017-12-30 10:33:33")).abnormality());
    }

    @Test
    public void persistDuring() throws Exception {
        Date date1 = dateFormat.parse("2017-06-20 10:33:33");
        Date date2 = dateFormat.parse("2017-06-30 10:33:33");
        Date date3 = dateFormat.parse("2017-10-30 10:33:33");
        assertNotNull(glucosetimeLine.persistDuring(date1, date2));
        assertNull(glucosetimeLine.persistDuring(date2, date3));
        assertEquals(hpoTermMap.get("Hyperglycemia").getId(), glucosetimeLine.persistDuring(date1, date2).abnormality());
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
        glucosetimeLine.occurredDuring(date0, date5).forEach(p -> {
            System.out.println(p.abnormality().getValue() + "\t" + p.effectiveStart() + "\t" + p.effectiveEnd());
        });

    }

}
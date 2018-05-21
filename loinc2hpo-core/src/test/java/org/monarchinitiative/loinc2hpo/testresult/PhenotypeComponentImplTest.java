package org.monarchinitiative.loinc2hpo.testresult;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.fhir.FhirObservationAnalyzerTest;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.formats.hpo.HpoTerm;
import org.monarchinitiative.phenol.io.obo.hpo.HpoOboParser;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class PhenotypeComponentImplTest {

    private static PhenotypeComponent testComponent;
    private static Map<String, HpoTerm> hpoTermMap;
    private static Map<TermId, HpoTerm> hpoTermMap2;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @BeforeClass
    public static void setup() throws Exception{
        String hpo_obo = FhirObservationAnalyzerTest.class.getClassLoader().getResource("obo/hp.obo").getPath();
        HpoOboParser hpoOboParser = new HpoOboParser(new File(hpo_obo));
        HpoOntology hpo = null;
        try {
            hpo = hpoOboParser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImmutableMap.Builder<String,HpoTerm> termmap = new ImmutableMap.Builder<>();
        ImmutableMap.Builder<TermId,HpoTerm> termmap2 = new ImmutableMap.Builder<>();
        if (hpo !=null) {
            List<HpoTerm> res = hpo.getTermMap().values().stream().distinct()
                    .collect(Collectors.toList());
            res.forEach( term -> {
                termmap.put(term.getName(),term);
                termmap2.put(term.getId(), term);
            });
        }
        hpoTermMap = termmap.build();
        hpoTermMap2 = termmap2.build();
    }

    @Before
    public void init() throws Exception {
        //create an instance
        Date start = dateFormat.parse("2016-09-30 09:30:00");
        testComponent = new PhenotypeComponentImpl.Builder()
                .start(start)
                .hpoTerm(hpoTermMap.get("Hyperglycemia"))
                .isNegated(false)
                .build();

    }

    @Test
    public void effectiveStart() throws Exception {
        assertEquals("2016-09-30 09:30:00", dateFormat.format(testComponent.effectiveStart()));
    }

    @Test
    public void effectiveEnd() throws Exception {
        assertTrue(testComponent.effectiveStart().before(testComponent.effectiveEnd()));
    }

    @Test
    public void isEffective() throws Exception {
        //assertTrue(testComponent.effectiveStart().equals(dateFormat.parse("2016-09-30 09:30:00")));
        assertFalse(testComponent.isEffective(dateFormat.parse("2016-09-29 10:00:00")));
        assertTrue(testComponent.isEffective(dateFormat.parse("2016-09-30 09:30:00")));
        assertTrue(testComponent.isEffective(dateFormat.parse("2016-09-30 09:30:01")));
        assertTrue(testComponent.isEffective(dateFormat.parse("2016-09-30 10:30:00")));
        assertTrue(testComponent.isEffective(dateFormat.parse("2020-09-30 09:30:01")));
    }

    @Test
    public void abnormality() throws Exception {
        assertEquals("Hyperglycemia", testComponent.abnormality().getName());
    }

    @Test
    public void isNegated() throws Exception {
        assertEquals(false, testComponent.isNegated());
    }

    @Test
    public void changeEffectiveStart() throws Exception {
        assertTrue(testComponent.isEffective(dateFormat.parse("2016-09-30 09:30:01")));
        testComponent.changeEffectiveStart(dateFormat.parse("2016-09-30 10:30:01"));
        assertFalse(testComponent.isEffective(dateFormat.parse("2016-09-30 09:30:01")));
    }

    @Test
    public void changeEffectiveEnd() throws Exception {
        assertTrue(testComponent.isEffective(dateFormat.parse("2020-09-30 09:30:01")));
        testComponent.changeEffectiveEnd(dateFormat.parse("2018-09-30 09:30:01"));
        assertFalse(testComponent.isEffective(dateFormat.parse("2020-09-30 09:30:01")));
    }

    @Test
    public void persistingDuring() throws Exception {
        Date start = dateFormat.parse("2018-09-30 09:30:01");
        Date end = dateFormat.parse("2028-10-30 09:45:56");
        assertFalse(testComponent.isPersistingDuring(dateFormat.parse("1999-09-20 09:00:00"), dateFormat.parse("2001-09-10 10:44:34")));
        assertFalse(testComponent.isPersistingDuring(dateFormat.parse("1999-09-20 09:00:00"), start));
        assertTrue(testComponent.isPersistingDuring(start, end));
        testComponent.changeEffectiveEnd(dateFormat.parse("2018-09-30 09:30:01"));
        assertFalse(testComponent.isPersistingDuring(start, end));
        assertTrue(testComponent.isPersistingDuring(start, start));
    }

    @Test
    public void occurredDuring() throws Exception {
        Date date00 = dateFormat.parse("2014-09-30 10:33:33");
        Date date0 = dateFormat.parse("2016-05-30 10:33:33");
        Date date1 = dateFormat.parse("2018-09-30 10:33:33");
        Date date2 = dateFormat.parse("2018-10-30 10:33:33");
        Date date3 = dateFormat.parse("2018-11-30 10:33:33");
        Date date4 = dateFormat.parse("2018-12-30 10:33:33");
        testComponent.changeEffectiveEnd(date2);

        assertFalse(testComponent.occurredDuring(date00, date0));
        assertTrue(testComponent.occurredDuring(date0, testComponent.effectiveStart()));
        assertTrue(testComponent.occurredDuring(date0, date1));
        assertTrue(testComponent.occurredDuring(date1, date3));
        assertTrue(testComponent.occurredDuring(date2, date3));
        assertFalse(testComponent.occurredDuring(date3, date4));


    }

}
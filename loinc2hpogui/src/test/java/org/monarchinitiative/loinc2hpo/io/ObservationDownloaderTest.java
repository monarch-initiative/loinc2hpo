package org.monarchinitiative.loinc2hpo.io;

import org.hl7.fhir.dstu3.model.Observation;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.io.ObservationDownloader;

import java.util.List;

import static org.junit.Assert.*;

public class ObservationDownloaderTest {
    @Test
    public void retrieveObservation() throws Exception {
        String testLoinc = "1558-6";
        List<Observation> observations = ObservationDownloader.retrieveObservation( testLoinc);
        //data on server might change, so the assertion is not always true even through everything works
        assertEquals(4, observations.size());

        String testLoinc2 = "600-7";
        observations = ObservationDownloader.retrieveObservation(testLoinc2);
        assertEquals(185, observations.size());

    }

    @Test
    public void longestObservation() throws Exception {
        String testLoinc = "600-7";
        String longestObservation = ObservationDownloader.longestObservation(ObservationDownloader.retrieveObservation(testLoinc));
        if (longestObservation != null) {
            System.out.println(longestObservation);
        }
    }

    @Test
    public void firstComplete() throws Exception{
        String testLoinc = "600-7";
        List<Observation> observations = ObservationDownloader.retrieveObservation(testLoinc);
        String firstComplete = ObservationDownloader.firstCompleteObservation(observations);
        if (firstComplete != null) {
            System.out.println(firstComplete);
        } else {
            System.out.println("Could not find a complete record that has measured value, reference range and interpretation");
        }
    }

    @Test
    public void firstAccetable() throws Exception {
        String testLoinc = "600-7";
        List<Observation> observations = ObservationDownloader.retrieveObservation(testLoinc);
        String firstAcceptable = ObservationDownloader.firstAcceptableObservation(observations);
        if (firstAcceptable != null) {
            System.out.println(firstAcceptable);
        } else {
            System.out.println("Could not find an acceptable observation");
        }

    }

    @Test
    public void retrievePatient() throws Exception {
    }

}
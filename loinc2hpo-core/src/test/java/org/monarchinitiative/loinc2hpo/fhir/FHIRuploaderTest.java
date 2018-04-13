package org.monarchinitiative.loinc2hpo.fhir;

import com.github.javafaker.Faker;
import org.junit.Test;

import java.util.Random;
import java.util.stream.DoubleStream;


public class FHIRuploaderTest {

    @Test
    public void test() {
        Faker faker = new Faker();

        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        
    }



}
package org.monarchinitiative.loinc2hpo.fhir;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import edu.emory.mathcs.backport.java.util.Collections;

import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Observation.ObservationReferenceRangeComponent;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Range;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;
import org.joda.time.DateTime;
import org.monarchinitiative.loinc2hpo.util.RandomGenerator;
import org.monarchinitiative.loinc2hpo.util.RandomGeneratorImpl;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class FakerWrapper {

    private RandomGenerator randomGenerator = new RandomGeneratorImpl();
    private Faker faker;

    public FakerWrapper() {
        this.faker = new Faker();
    }

    HumanName fakeName() {
        Name name = faker.name();
        return new HumanName().setUse(HumanName.NameUse.OFFICIAL).setFamily(name.lastName()).addGiven(name.firstName());
    }

    Address fakeAddress() {
        com.github.javafaker.Address fakeAddr = faker.address();
        //String streetLine = address.streetAddress() + address.streetAddress(true);
        return new Address().addLine(fakeAddr.streetAddress(true)).setCity(fakeAddr.city())
                .setState(fakeAddr.state()).setPostalCode(fakeAddr.zipCode()).setCountry(fakeAddr.country());
    }

    Patient.ContactComponent fakeContact() {
        CodeableConcept relationship = new CodeableConcept();
        Coding coding1 = new Coding();
        coding1.setSystem(faker.internet().url()).setCode(String.valueOf(randomGenerator.randUpperCaseChar()));
        relationship.addCoding(coding1);
        Coding coding2 = new Coding();
        coding2.setSystem(faker.internet().url()).setCode(String.valueOf(randomGenerator.randUpperCaseChar()));

        Reference ref = new Reference();
        ref.setReference(faker.company().name() + "/" + randomGenerator.randInt(1, 100));

        return new Patient.ContactComponent().addRelationship(relationship).setAddress(fakeAddress()).setOrganization(ref);
    }

    ContactPoint fakeContactPhone() {
        return new ContactPoint()
                .setSystem(ContactPoint.ContactPointSystem.PHONE)
                .setValue(faker.phoneNumber().phoneNumber())
                .setPeriod(fakePeriod());
    }

    ContactPoint fakeContactEmail() {
        return new ContactPoint()
                .setSystem(ContactPoint.ContactPointSystem.EMAIL)
                .setValue(faker.internet().emailAddress())
                .setPeriod(fakePeriod());
    }

    Identifier fakeIdentifier() {
        IdentifierUse[] ids = Identifier.IdentifierUse.values();
        return new Identifier()
                .setUse(ids[randomGenerator.randInt(0, ids.length)])
                .setSystem(faker.internet().url())
                .setValue(randomGenerator.randString(4,4, false))
                .setPeriod(fakePeriod());
    }

    Date fakeBirthday() {
        return faker.date().birthday();
    }

    Date fakeDate(int year) {
        return new DateTime(year,                                   //year
                randomGenerator.randInt(1, 13),    //random month
                randomGenerator.randInt(1, 29),    //random date
                randomGenerator.randInt(1, 24),    //random hour
                randomGenerator.randInt(0, 60),    //random minute
                randomGenerator.randInt(0, 1000),  //random second
                randomGenerator.randInt(0, 1000))  //random millisecond
                .toDate();
    }

    Date fakeDate21Century() {
        return fakeDate(2000, 2017);
    }

    Date fakeDate() {
        return fakeDate(1900, 2017);
    }

    Date fakeDate(int startYear, int endYear) {
        return new DateTime(
                randomGenerator.randInt(startYear, endYear),//random year
                randomGenerator.randInt(1, 13),    //random month
                randomGenerator.randInt(1, 29),    //random date
                randomGenerator.randInt(1, 24),    //random hour
                randomGenerator.randInt(0, 60),    //random minute
                randomGenerator.randInt(0, 60),  //random second
                randomGenerator.randInt(0, 1000))  //random millisecond
                .toDate();
    }

    Date fakeDateBetween(Date from, Date to) {
        return faker.date().between(from, to);
    }

    Period fakePeriod() {
        Date date1 = fakeDate21Century();
        Date date2 = fakeDate21Century();
        if (date1.before(date2)) {
            return new Period().setStart(date1).setEnd(date2);
        } else {
            return new Period().setStart(date2).setEnd(date1);
        }
    }

    Coding fakeCoding() {
        return new Coding()
                .setSystem(faker.internet().url())
                .setCode(randomGenerator.randString(2,2,false));
    }

    Range fakeRange(int lowBound, int upperBound, String unit) {
        List<Double> values = randomGenerator.randDoubles(2, lowBound, upperBound);
        Collections.sort(values);
        double low = values.get(0);
        double high = values.get(1);
        String unitSystem = faker.internet().url();
        SimpleQuantity lowQ = new SimpleQuantity();
        lowQ.setValue(low)
                .setUnit(unit)
                .setSystem(unitSystem)
                .setCode(unit);
        SimpleQuantity highQ = new SimpleQuantity();
        highQ.setValue(high)
                .setUnit(unit)
                .setSystem(unitSystem)
                .setCode(unit);
        return new Range().setLow(lowQ).setHigh(highQ);
    }

    ObservationReferenceRangeComponent fakeReferenceRangeComponent(double lowBound, double upperBound, String unit) {
        //
        List<Double> values = randomGenerator.randDoubles(2, lowBound, upperBound);
        Collections.sort(values);
        double low = values.get(0);
        double high = values.get(1);
        String unitSystem = faker.internet().url();
        SimpleQuantity lowQ = new SimpleQuantity();
        lowQ.setValue(low)
                .setUnit("fake unit")
                .setSystem(unitSystem)
                .setCode(unit);
        SimpleQuantity highQ = new SimpleQuantity();
        highQ.setValue(high)
                .setUnit("fake unit")
                .setSystem(unitSystem)
                .setCode(unit);

        Range ageRange = fakeRange(10, 60, "YEAR");

        CodeableConcept appliesTo = new CodeableConcept();
        appliesTo.addCoding(fakeCoding()).addCoding(fakeCoding());

        return new ObservationReferenceRangeComponent()
                .setLow(lowQ)
                .setHigh(highQ)
                .setAge(ageRange)
                .addAppliesTo(appliesTo);
    }

}

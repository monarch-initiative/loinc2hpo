package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;

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
import org.monarchinitiative.loinc2hpocore.legacy.util.RandomGenerator;
import org.monarchinitiative.loinc2hpocore.legacy.util.RandomGeneratorImpl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FhirResourceComponentFaker {

    private RandomGenerator randomGenerator = new RandomGeneratorImpl();
    private Faker faker;

    public FhirResourceComponentFaker() {
        this.faker = new Faker();
    }

    public HumanName fakeName() {
        Name name = faker.name();
        return new HumanName().setUse(HumanName.NameUse.OFFICIAL).setFamily(name.lastName()).addGiven(name.firstName());
    }

    public Address fakeAddress() {
        com.github.javafaker.Address fakeAddr = faker.address();
        //String streetLine = address.streetAddress() + address.streetAddress(true);
        return new Address().addLine(fakeAddr.streetAddress(true)).setCity(fakeAddr.city())
                .setState(fakeAddr.state()).setPostalCode(fakeAddr.zipCode()).setCountry(fakeAddr.country());
    }

    public Patient.ContactComponent fakeContact() {
        CodeableConcept relationship = new CodeableConcept();
        Coding coding1 = new Coding();
        coding1.setSystem(faker.internet().url()).setCode(String.valueOf(randomGenerator.randUpperCaseChar()));
        relationship.addCoding(coding1);
        Coding coding2 = new Coding();
        coding2.setSystem(faker.internet().url()).setCode(String.valueOf(randomGenerator.randUpperCaseChar()));
        relationship.addCoding(coding2);

        Reference ref = new Reference();
        ref.setReference(faker.company().name() + "/" + randomGenerator.randInt(1, 100));

        return new Patient.ContactComponent().addRelationship(relationship).setAddress(fakeAddress());
        //.setOrganization(ref);
    }

    public ContactPoint fakeContactPhone() {
        return new ContactPoint()
                .setSystem(ContactPoint.ContactPointSystem.PHONE)
                .setValue(faker.phoneNumber().phoneNumber())
                .setPeriod(fakePeriod());
    }

    public ContactPoint fakeContactEmail() {
        return new ContactPoint()
                .setSystem(ContactPoint.ContactPointSystem.EMAIL)
                .setValue(faker.internet().emailAddress())
                .setPeriod(fakePeriod());
    }

    public Identifier fakeIdentifier() {
        IdentifierUse[] ids = {IdentifierUse.OFFICIAL, IdentifierUse.SECONDARY, IdentifierUse.TEMP, IdentifierUse.USUAL};
        return new Identifier()
                .setUse(ids[randomGenerator.randInt(0, ids.length)])
                .setSystem(faker.internet().url())
                .setValue(randomGenerator.randString(4,4, false))
                .setPeriod(fakePeriod());
    }

    public Date fakeBirthday() {
        return faker.date().birthday();
    }

    public LocalDate fakeDate(int year) {
        return LocalDate.of(year,                                   //year
                randomGenerator.randInt(1, 13),    //random month
                randomGenerator.randInt(1, 29)) ; //random millisecond

    }
    public LocalDate fakeDate() {
        return fakeDate(1900, 2017);
    }

    public LocalDate fakeDate(int startYear, int endYear) {
        return LocalDate.of(
                randomGenerator.randInt(startYear, endYear),//random year
                randomGenerator.randInt(1, 13),    //random month
                randomGenerator.randInt(1, 29));  //random millisecond

    }

    public LocalDate fakeDate21Century() {
        return fakeDate(2000, 2017);
    }

    public Date fakeDateBetween(Date from, Date to) {
        return faker.date().between(from, to);
    }

    public Period fakePeriod() {
        LocalDate ldate1 = fakeDate21Century();
        LocalDate ldate2 = fakeDate21Century();
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Date date1 = Date.from(ldate1.atStartOfDay(defaultZoneId).toInstant());
        Date date2 = Date.from(ldate2.atStartOfDay(defaultZoneId).toInstant());
        if (date1.before(date2)) {
            return new Period().setStart(date1).setEnd(date2);
        } else {
            return new Period().setStart(date2).setEnd(date1);
        }
    }

    public Coding fakeCoding() {
        return new Coding()
                .setSystem(faker.internet().url())
                .setCode(randomGenerator.randString(2,2,false));
    }

    public Range fakeRange(int lowBound, int upperBound, String unit) {
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

    public ObservationReferenceRangeComponent fakeReferenceRangeComponent(double lowBound, double upperBound, String unit) {
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

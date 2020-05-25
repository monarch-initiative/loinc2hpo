package org.monarchinitiative.loinc2hpocore.fhir;

import ca.uhn.fhir.model.primitive.IdDt;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.monarchinitiative.loinc2hpocore.util.RandomGenerator;
import org.monarchinitiative.loinc2hpocore.util.RandomGeneratorImpl;
import org.monarchinitiative.loinc2hpocore.Constants;
import org.monarchinitiative.loinc2hpocore.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.loinc2hpocore.loinc.LoincScale;

import java.util.*;
import java.util.stream.Collectors;


/**
 * This class is responsible for generating synthetic FHIR resources
 */
public class FhirResourceFakerImpl implements FhirResourceFaker {

    private RandomGenerator randomGenerator;
    private FhirResourceComponentFaker fhirResourceComponentFaker = new FhirResourceComponentFaker();
    private Map<LoincId, LoincEntry> loincEntryMap;

    /**
     * Constructor takes in a loincEntry map, which is created by calling method {@link LoincEntry#getLoincEntryMap(String)}  getLoincEntryMap}.
     * @param loincEntryMap
     */
    public FhirResourceFakerImpl(Map<LoincId, LoincEntry> loincEntryMap) {

        this.randomGenerator = new RandomGeneratorImpl();
        this.loincEntryMap = loincEntryMap;
    }

    @Override
    public Patient fakePatient() {
        Patient patient = new Patient();

        //fake patient id with prefix: "Patient/"
        //patient.setId("Patient/" + randomGenerator.randString(1, 3, true));
        //replace the above line with a built-in fake id generator
        patient.setId(IdDt.newRandomUuid());

        patient.addIdentifier(fhirResourceComponentFaker.fakeIdentifier()).addIdentifier(fhirResourceComponentFaker.fakeIdentifier());

        patient.setActive(randomGenerator.randBoolean());

        patient.addName(fhirResourceComponentFaker.fakeName());

        patient.setGender(randomGenerator.randBoolean() ? Enumerations.AdministrativeGender.MALE : Enumerations.AdministrativeGender.FEMALE);

        patient.setBirthDate(fhirResourceComponentFaker.fakeBirthday());

        patient.addAddress(fhirResourceComponentFaker.fakeAddress());

        patient.addTelecom(fhirResourceComponentFaker.fakeContactPhone())
                .addTelecom(fhirResourceComponentFaker.fakeContactEmail());

        patient.addContact(fhirResourceComponentFaker.fakeContact());

        return patient;
    }

    @Override
    public List<Patient> fakePatients(int num) {
        List<Patient> patientList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            patientList.add(fakePatient());
        }

        return patientList;
    }

    @Override
    public Observation fakeObservation() {

        throw new UnsupportedOperationException();
    }

    @Override
    public Observation fakeObservation(LoincId loincId, Patient patient) {
        Observation observation = null;
        observation = new Observation();

        //add a fake id
        observation.setId(randomGenerator.randString(1, 3, true));

        //add two fake identifiers
        observation.addIdentifier(fhirResourceComponentFaker.fakeIdentifier())
                .addIdentifier(fhirResourceComponentFaker.fakeIdentifier());

        //set a fake status
        ObservationStatus[] statuses = {ObservationStatus.FINAL, ObservationStatus.CORRECTED, ObservationStatus.AMENDED, ObservationStatus.PRELIMINARY, ObservationStatus.REGISTERED};
        observation.setStatus(statuses[randomGenerator.randInt(0, statuses.length)]);

        //set a fake code with two faking codings
        CodeableConcept code = new CodeableConcept();
        Coding loinc = new Coding()
                .setSystem(Constants.LOINCSYSTEM)
                .setCode(loincId.toString())
                .setDisplay(loincEntryMap.get(loincId).getLongName());
        Coding randCode = fhirResourceComponentFaker.fakeCoding();
        code.addCoding(loinc).addCoding(randCode);
        observation.setCode(code);

        //add subject
        Reference subject = new Reference();
        subject.setReference(patient.getId());
        observation.setSubject(subject);

        //set effective period
        Period effective = fhirResourceComponentFaker.fakePeriod();
        observation.setEffective(effective);

        //set issue date
        observation.setIssued(fhirResourceComponentFaker.fakeDateBetween(effective.getStart(), effective.getEnd()));

        //add perform
        //Reference performer = new Reference()
        //        .setReference("Practitioner/" + randomGenerator.randString(1, 3, true));
        //observation.addPerformer(performer);



        //for Qn, add measured value and add interpretation code
        if (LoincScale.string2enum(loincEntryMap.get(loincId).getScale()) == LoincScale.Qn) {
            //add a reference range
            Observation.ObservationReferenceRangeComponent referenceRange = fhirResourceComponentFaker.fakeReferenceRangeComponent(4, 8, "fake unit");
            observation.addReferenceRange(referenceRange);

            //add measured value
            int outcome = randomGenerator.randInt(-1, 2); //possible outcome: -1, 0, 1
            double measuredValue;
            double ref_low = referenceRange.getLow().getValue().doubleValue();
            double ref_high = referenceRange.getHigh().getValue().doubleValue();
            String interpretationCode;
            switch (outcome) {
                case -1:
                    measuredValue = randomGenerator.randDouble(ref_low -2, ref_low);
                    interpretationCode = "L";
                    break;
                case 0:
                    measuredValue = randomGenerator.randDouble(ref_low, ref_high);
                    interpretationCode = "N";
                    break;
                case 1:
                    measuredValue = randomGenerator.randDouble(ref_high, ref_high + 2);
                    interpretationCode = "H";
                    break;
                default:
                    measuredValue = Double.MIN_VALUE;
                    interpretationCode = "U";
            }
            SimpleQuantity measuredQ = new SimpleQuantity();
            measuredQ.setValue(measuredValue)
                    .setCode(referenceRange.getHigh().getCode())
                    .setSystem(referenceRange.getHigh().getSystem())
                    .setUnit(referenceRange.getHigh().getUnit());
            observation.setValue(measuredQ);

            boolean toAddInterpretation = randomGenerator.randBoolean();
            if (toAddInterpretation) {
                CodeableConcept interpretation = new CodeableConcept();
                Coding interpCoding = new Coding();
                interpCoding.setSystem(Constants.V2OBSERVATIONINTERPRETATION)
                        .setCode(interpretationCode);
                interpretation.addCoding(interpCoding);
                observation.setInterpretation(interpretation);
            }

        }

        else if (LoincScale.string2enum(loincEntryMap.get(loincId).getScale()) == LoincScale.Ord &&
                loincEntryMap.get(loincId).isPresentOrd()) {
            //add measured value: it is usually a threshold with an indication whether measured value below or above it
            double threshold = randomGenerator.randDouble(0.01, 0.1); //possible outcome: -1, 0, 1
            Quantity.QuantityComparator comparator;
            String interpretationCode;
            boolean belowThreshold = randomGenerator.randBoolean();
            if (belowThreshold) {
                comparator = Quantity.QuantityComparator.LESS_THAN;
                interpretationCode = "NEG";
            } else {
                interpretationCode = "POS";
                comparator = Quantity.QuantityComparator.GREATER_THAN;
            }
            SimpleQuantity measuredQ = new SimpleQuantity();
            Coding unitCoding = fhirResourceComponentFaker.fakeCoding();
            measuredQ.setValue(threshold)
                    .setComparator(comparator)
                    .setUnit("fake unit")
                    .setSystem(unitCoding.getSystem())
                    .setUnit(unitCoding.getCode());
            observation.setValue(measuredQ);

            boolean toAddInterpretation = randomGenerator.randBoolean();
            if (toAddInterpretation) {
                CodeableConcept interpretation = new CodeableConcept();
                Coding interpCoding = new Coding();
                interpCoding.setSystem(Constants.V2OBSERVATIONINTERPRETATION)
                        .setCode(interpretationCode);
                interpretation.addCoding(interpCoding);
                observation.setInterpretation(interpretation);
            }
        }


        else {
            CodeableConcept result = new CodeableConcept();
            Coding outcome1 = fhirResourceComponentFaker.fakeCoding();
            Coding outcome2 = fhirResourceComponentFaker.fakeCoding();
            result.addCoding(outcome1).addCoding(outcome2);
            observation.setValue(result);
        }

        return observation;
    }



    @Override
    public Map<Patient, List<Observation>> fakeObservations(List<Patient> patientList, List<LoincId> loincList) {
        Map<Patient, List<Observation>> patientObservationListMap = new HashMap<>();
        patientList.forEach(p -> { //for each patient, create a list of observations
            List<Observation> observationList = loincList //create a list of observations with the LOINC list
                    .stream()
                    .map(loincId -> fakeObservation(loincId, p))
                    .collect(Collectors.toList());
            patientObservationListMap.put(p, observationList);
        });

        return patientObservationListMap;
    }


}

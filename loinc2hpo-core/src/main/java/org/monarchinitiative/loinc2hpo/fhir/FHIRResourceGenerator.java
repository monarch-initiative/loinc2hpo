package org.monarchinitiative.loinc2hpo.fhir;


import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.monarchinitiative.loinc2hpo.Constants;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincScale;
import org.monarchinitiative.loinc2hpo.util.RandomGenerator;
import org.monarchinitiative.loinc2hpo.util.RandomGeneratorImpl;
import java.util.*;
import java.util.stream.Collectors;


/**
 * This class is responsible for generating synthetic FHIR resources
 */
public class FHIRResourceGenerator {

    private RandomGenerator randomGenerator;
    private FakerWrapper fakerWrapper = new FakerWrapper();
    private Map<LoincId, LoincEntry> loincEntryMap;

    /**
     * Constructor takes in a loincEntry map, which is created by calling method {@link org.monarchinitiative.loinc2hpo.loinc.LoincEntry#getLoincEntryList(String)}  getLoincEntryList}.
     * @param loincEntryMap
     */
    public FHIRResourceGenerator(Map<LoincId, LoincEntry> loincEntryMap) {

        this.randomGenerator = new RandomGeneratorImpl();
        this.loincEntryMap = loincEntryMap;
    }

    /**
     * A enum of some LOINC that have been annotated to HPO terms.
     */
    public enum LOINCEXAMPLE{
        SERUM_POTASSIUM {
            @Override
            public String toString() {
                return "2823-3";
            }
        },
        HEMOGLOBIN {
            @Override
            public String toString() {
                return "718-7";
            }
        },
        SERUMCREATININE{
            @Override
            public String toString() {
                return "2160-0";
            }
        },
        SERUMCHLORIDE{
            @Override
            public String toString() {
                return "2075-0";
            }
        },
        SERUMGLUCOSE {
            @Override
            public String toString() {
                return "2345-7";
            }
        },
        PLATELETCOUNT{
            @Override
            public String toString() {
                return "777-3";
            }
        },
        PROTEINURINE{
            @Override
            public String toString() {
                return "20454-5";
            }
        },
        ANISOCYTOSIS {
            @Override
            public String toString() {
                return "702-1";
            }
        },
        KETONEURIN {
            @Override
            public String toString() {
                return "2514-8";
            }
        },
        URINECOLOR {
            @Override
            public String toString() {
                return "5778-6";
            }
        }

    }

    List<String> loincs = Arrays.stream(LOINCEXAMPLE.values()).map(Enum::toString).collect(Collectors.toList());


    /**
     * Return the list of example LOINC in the enum class {@link LOINCEXAMPLE LOINCEXAMPLE}.
     * @return
     */
    public List<LoincId> loincExamples() {
        return Arrays.stream(LOINCEXAMPLE.values()).map(p -> {
            try {
                return new LoincId(p.toString());
            } catch (MalformedLoincCodeException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

    /**
     * Generate a Observation object for specified LOINC and subject. The values of the observation is randomly generated.
     * @param loincId
     * @param patient subject of the observation
     * @return an observation object
     */
    public Observation generateObservation(LoincId loincId, Patient patient) {
        Observation observation = null;
        observation = new Observation();

        //add a fake id
        observation.setId(randomGenerator.randString(1, 3, true));

        //add two fake identifiers
        observation.addIdentifier(fakerWrapper.fakeIdentifier())
                .addIdentifier(fakerWrapper.fakeIdentifier());

        //set a fake status
        ObservationStatus[] statuses = Observation.ObservationStatus.values();
        observation.setStatus(statuses[randomGenerator.randInt(0, statuses.length)]);

        //set a fake code with two faking codings
        CodeableConcept code = new CodeableConcept();
        Coding loinc = new Coding()
                .setSystem(Constants.LOINCSYSTEM)
                .setCode(loincId.toString())
                .setDisplay(loincEntryMap.get(loincId).getLongName());
        Coding randCode = fakerWrapper.fakeCoding();
        code.addCoding(loinc).addCoding(randCode);
        observation.setCode(code);

        //add subject
        Reference subject = new Reference();
        subject.setReference(patient.getId());
        observation.setSubject(subject);

        //set effective period
        Period effective = fakerWrapper.fakePeriod();
        observation.setEffective(effective);

        //set issue date
        observation.setIssued(fakerWrapper.fakeDateBetween(effective.getStart(), effective.getEnd()));

        //add perform
        Reference performer = new Reference()
                .setReference("Practitioner/" + randomGenerator.randString(1, 3, true));
        observation.addPerformer(performer);



        //for Qn, add measured value and add interpretation code
        if (LoincScale.string2enum(loincEntryMap.get(loincId).getScale()) == LoincScale.Qn) {
            //add a reference range
            Observation.ObservationReferenceRangeComponent referenceRange = fakerWrapper.fakeReferenceRangeComponent(4, 8, "fake unit");
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
            Coding unitCoding = fakerWrapper.fakeCoding();
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
            Coding outcome1 = fakerWrapper.fakeCoding();
            Coding outcome2 = fakerWrapper.fakeCoding();
            result.addCoding(outcome1).addCoding(outcome2);
            observation.setValue(result);
        }

        return observation;
    }

    /**
     * Generate a list of Patient objects. Patient information is randomly generated.
     * @param num size of the list
     * @return a list of fake patients with specified size
     */
    public List<Patient> generatePatient(int num) {

        List<Patient> patientList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Patient patient = new Patient();

            //fake patient id with prefix: "Patient/"
            patient.setId("Patient/" + randomGenerator.randString(1, 3, true));

            patient.addIdentifier(fakerWrapper.fakeIdentifier()).addIdentifier(fakerWrapper.fakeIdentifier());

            patient.setActive(randomGenerator.randBoolean());

            patient.addName(fakerWrapper.fakeName());

            patient.setGender(randomGenerator.randBoolean() ? Enumerations.AdministrativeGender.MALE : Enumerations.AdministrativeGender.FEMALE);

            patient.setBirthDate(fakerWrapper.fakeBirthday());

            patient.addAddress(fakerWrapper.fakeAddress());

            patient.addTelecom(fakerWrapper.fakeContactPhone())
                    .addTelecom(fakerWrapper.fakeContactEmail());

            patient.addContact(fakerWrapper.fakeContact());


            patientList.add(patient);

        }

        return patientList;
    }

    /**
     * Specify a list of patients and LOINC, create fake observations for all LOINC for every patient
     * @param patientList
     * @param loincList
     * @return a map of patient: observations
     */
    public Map<Patient, List<Observation>> randPatientAndObservation(List<Patient> patientList, List<LoincId> loincList) {

        Map<Patient, List<Observation>> patientObservationListMap = new HashMap<>();
        patientList.forEach(p -> { //for each patient, create a list of observations
            List<Observation> observationList = loincList //create a list of observations with the LOINC list
                    .stream()
                    .map(loincId -> generateObservation(loincId, p))
                    .collect(Collectors.toList());
            patientObservationListMap.put(p, observationList);
        });

        return patientObservationListMap;
    }

}

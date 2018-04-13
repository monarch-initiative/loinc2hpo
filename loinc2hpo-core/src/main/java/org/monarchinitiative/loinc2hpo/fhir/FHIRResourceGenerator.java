package org.monarchinitiative.loinc2hpo.fhir;

import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.monarchinitiative.loinc2hpo.util.RandomGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * This class is responsible for generating synthetic FHIR resources
 */
public class FHIRResourceGenerator {

    private RandomGenerator randomGenerator;

    public FHIRResourceGenerator(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

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


    public Observation generateObservation(String loinc) {
        Observation observation = null;
        observation = new Observation();






        return observation;
    }

    public List<Patient> generatePatient(int num) {

        List<Patient> patientList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Patient patient = new Patient();
            patientList.add(patient);

            patient.setId(randomGenerator.randString(1, 3, true));

            Identifier id1 = new Identifier();
            String id1_value = randomGenerator.randString(6, 4, true);
            id1.setSystem("http://jax.test.org")
                    .setUse(Identifier.IdentifierUse.TEMP)
                    .setValue(id1_value);
            Identifier id2 = new Identifier();
            String id2_value = randomGenerator.randString(6, 4, true);
            id2.setSystem("http://test.com")
                    .setUse(Identifier.IdentifierUse.OFFICIAL)
                    .setValue(id2_value);
            patient.setIdentifier(Arrays.asList(id1, id2));

            patient.setActive(randomGenerator.randBoolean());

            HumanName name = new HumanName();

        }




        return patientList;
    }
}

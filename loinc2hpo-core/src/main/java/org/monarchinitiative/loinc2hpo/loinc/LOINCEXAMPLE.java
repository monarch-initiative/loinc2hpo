package org.monarchinitiative.loinc2hpo.loinc;

import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A enum of some LOINC that have been annotated to HPO terms. It is used to create fake resources.
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
    };


    /**
     * Return the list of example LOINC in the enum class.
     * @return
     */
    public static List<LoincId> loincExamples() {
        return Arrays.stream(LOINCEXAMPLE.values()).map(p -> {
            try {
                return new LoincId(p.toString());
            } catch (MalformedLoincCodeException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

}



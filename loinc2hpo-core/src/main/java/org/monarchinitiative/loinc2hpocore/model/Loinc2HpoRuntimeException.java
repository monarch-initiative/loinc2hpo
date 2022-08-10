package org.monarchinitiative.loinc2hpocore.model;

public class Loinc2HpoRuntimeException extends RuntimeException {


    public Loinc2HpoRuntimeException() { super();}
    public Loinc2HpoRuntimeException(String msg) { super(msg);}



    public static Loinc2HpoRuntimeException unrecognizedCode(String code) {
        return new Loinc2HpoRuntimeException("Unrecognized result code: \"" + code + "\"");
    }

    public static Loinc2HpoRuntimeException noCodeFound() {
        return new Loinc2HpoRuntimeException("No results code found.");
    }

    public static Loinc2HpoRuntimeException internalCodeNotFound(String externalCound) {
        return new Loinc2HpoRuntimeException("Could not find internal code to match \"" + externalCound + "\".");
    }


    public static Loinc2HpoRuntimeException ambiguousResults() {
        return new Loinc2HpoRuntimeException("Multiple matching codes found (should never happen).");
    }

    public static Loinc2HpoRuntimeException notAnnotated(LoincId loincId) {
        return new Loinc2HpoRuntimeException("Could not find annotation for " + loincId.toString());
    }

    public static Loinc2HpoRuntimeException outComenotAnnotated(LoincId loincId) {
        return new Loinc2HpoRuntimeException("Could not find annotation for " + loincId.toString());
    }

    public static Loinc2HpoRuntimeException malformedLoincCode(String line) {
        return new Loinc2HpoRuntimeException("malformedLoincCode: \"" + line + "\"");
    }

    public static Loinc2HpoRuntimeException subjectNotFound() {
        return new Loinc2HpoRuntimeException("Could not find subject element");
    }

    public static Loinc2HpoRuntimeException ambiguousSubject() {
        return new Loinc2HpoRuntimeException("Found more than one subject element");
    }

    public static Loinc2HpoRuntimeException referenceRangeNotFound() {
        return new Loinc2HpoRuntimeException("Did not find a reference range");
    }

    public static Loinc2HpoRuntimeException ambiguousReferenceRange() {
        return new Loinc2HpoRuntimeException("Found more than one reference range");
    }

    public static Loinc2HpoRuntimeException unrecognizedLoincCodeException() {
        return new Loinc2HpoRuntimeException("Unrecognize LOINC code");
    }

    public static Loinc2HpoRuntimeException loincCodeNotFound() {
        return new Loinc2HpoRuntimeException("LOINC code not found");
    }

    public static Loinc2HpoRuntimeException missingPanelComponent() {
        return new Loinc2HpoRuntimeException("Missing LOINC panel component");
    }


    public static Exception malFormedAnnotationLine(String line, int length) {
        return new Loinc2HpoRuntimeException(String.format("Malformed line with %d fields: %s", length, line));
    }
}

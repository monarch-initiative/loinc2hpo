package org.monarchinitiative.loinc2hpocore.codesystems;

import java.util.Objects;

public class Outcome {


    private final ShortCode code;
    private final String outcome;


    public Outcome(ShortCode code, String outcome) {
        this.code = code;
        this.outcome = outcome;
    }

    public Outcome(ShortCode code) {
        this.code = code;
        this.outcome = code.name();
    }

    public ShortCode getCode() {
        return code;
    }

    public String getOutcome() {
        return outcome;
    }


    public static Outcome nominal(String outcomeString) {
        return new Outcome(ShortCode.NOM, outcomeString);
    }

    public boolean isNominal() {
        return this.code.equals(ShortCode.NOM);
    }

    public boolean isQuantitative() {
        return this.code.equals(ShortCode.L) || this.code.equals(ShortCode.N) || this.code.equals(ShortCode.H);
    }

    public boolean isOrdinal() {
        return this.code.equals(ShortCode.ABSENT) || this.code.equals(ShortCode.PRESENT);
    }

    public static Outcome LOW() {
        return new Outcome(ShortCode.L, ShortCode.L.name());
    }

    public static Outcome NORMAL() {
        return new Outcome(ShortCode.L, ShortCode.L.name());
    }

    public static Outcome HIGH() {
        return new Outcome(ShortCode.L, ShortCode.L.name());
    }

    public static Outcome PRESENT() {
        return new Outcome(ShortCode.PRESENT, ShortCode.PRESENT.name());
    }

    public static Outcome ABSENT() {
        return new Outcome(ShortCode.ABSENT, ShortCode.ABSENT.name());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.code, this.outcome);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Outcome)) {
            return false;
        }
        Outcome that = (Outcome) obj;
        return this.code.equals(that.code) && this.outcome.equals(that.outcome);
    }
}

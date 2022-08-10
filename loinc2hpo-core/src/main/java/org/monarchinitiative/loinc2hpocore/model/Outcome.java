package org.monarchinitiative.loinc2hpocore.model;

import java.util.Objects;

public class Outcome implements Comparable<Outcome> {


    private final ShortCode code;
    private final String outcome;


    public Outcome(ShortCode code, String outcome) {
        this.code = code;
        this.outcome = outcome;
    }

    public Outcome(ShortCode code) {
        this.code = code;
        this.outcome = code.shortForm();
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
        return this.code.equals(ShortCode.NEG) || this.code.equals(ShortCode.POS);
    }

    public static Outcome LOW() {
        return new Outcome(ShortCode.L);
    }

    public static Outcome NORMAL() {
        return new Outcome(ShortCode.N);
    }

    public static Outcome HIGH() {
        return new Outcome(ShortCode.H);
    }

    public static Outcome POSITIVE() {
        return new Outcome(ShortCode.POS);
    }

    public static Outcome NEGATIVE() {
        return new Outcome(ShortCode.NEG);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.code, this.outcome);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Outcome that)) {
            return false;
        }
        return this.code.equals(that.code) && this.outcome.equals(that.outcome);
    }

    @Override
    public String toString() {
        if (code.equals(ShortCode.NOM)) {
            return "Nom: " + outcome;
        } else {
            return code.name();
        }
    }

    @Override
    public int compareTo(Outcome that) {
        int res = this.code.compareTo(that.code);
        return res != 0 ? res : this.outcome.compareTo(that.outcome);
    }
}

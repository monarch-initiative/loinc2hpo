package org.monarchinitiative.loinc2hpo.testresult;

import org.monarchinitiative.phenol.ontology.data.Term;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;

public class PhenotypeComponentImpl implements PhenotypeComponent {

    private Date start;
    private Date end;
    private Term hpoTerm;
    private boolean isNegated;

    private PhenotypeComponentImpl() {

    }

    private PhenotypeComponentImpl(@NotNull Date start, @Nullable Date end, @NotNull Term hpoTerm, boolean isNegated) {
        this.start = start;
        this.end = end;
        this.hpoTerm = hpoTerm;
        this.isNegated = isNegated;
    }

    @Override
    public Date effectiveStart() {
        return this.start;
    }

    @Override
    public Date effectiveEnd() {
        return this.end;
    }

    @Override
    public boolean isEffective(Date timepoint) {
        //at timepoint between [effectiveStart, effectiveEnd] inclusive is effective
        if (timepoint.before(start) || timepoint.after(end)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isPersistingDuring(Date start, Date end) {
        return !start.before(this.start) && !end.after(this.end);
    }

    @Override
    public boolean occurredDuring(Date start, Date end) {
        return !(start.after(this.end) || end.before(this.start));
    }


    @Override
    public Term abnormality() {
        return this.hpoTerm;
    }

    @Override
    public boolean isNegated() {
        return this.isNegated;
    }

    @Override
    public void changeEffectiveStart(Date start) {
        this.start = start;
    }

    @Override
    public void changeEffectiveEnd(Date end) {
        this.end = end;
    }

    public static class Builder {
        private Date start;
        private Date end;
        private Term hpoTerm;
        private boolean isNegated;

        protected Builder() {
            //default start year 0000
            //default end year 9999
            //SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
            //start = dateFormat.parse("0000-01-01 00:00:00");
            //end = dateFormat.parse("9999-12-31 23:59:59");

            //default start year: minimal integer
            Calendar startCal = Calendar.getInstance();
            startCal.set(Integer.MIN_VALUE, 1, 1, 0, 0, 0);
            start = startCal.getTime();

            //default end year: max integer
            Calendar endCal = Calendar.getInstance();
            endCal.set(Integer.MAX_VALUE, 12, 31, 23, 59, 59);
            end = endCal.getTime();
        }

        protected Builder start(Date start) {
            this.start = start;
            return this;
        }

        protected Builder end(Date end) {
            this.end = end;
            return this;
        }

        protected Builder hpoTerm(Term hpoTerm) {
            this.hpoTerm = hpoTerm;
            return this;
        }

        protected Builder isNegated(boolean isNegated) {
            this.isNegated = isNegated;
            return this;
        }


        protected PhenotypeComponentImpl build() {
            return new PhenotypeComponentImpl(this.start, this.end, this.hpoTerm, this.isNegated);
        }
    }
}

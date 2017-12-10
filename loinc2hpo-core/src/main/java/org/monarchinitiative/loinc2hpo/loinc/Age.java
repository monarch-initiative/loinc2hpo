package org.monarchinitiative.loinc2hpo.loinc;

public class Age {

    private final int n_day;
    private final int n_month;
    private final int n_year;

    public Age(Integer years, Integer months, Integer days) {
        n_year = years!=null?years:0;
        n_month = months!=null?months:0;
        n_day = days!=null?days:0;
    }

    @Override
    public String toString() {
        String ret= n_year>0 ? String.format("%dY",n_year):"";
        ret += n_month>0 ? String.format("%dM",n_month):"";
        ret += n_day>0 ? String.format("%sD",n_day):"";
        return ret;
    }




}

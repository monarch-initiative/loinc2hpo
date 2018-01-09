package org.monarchinitiative.loinc2hpo.loinc;

import org.monarchinitiative.loinc2hpo.exception.MaformedLoincCodeException;

public class LoincId {
    private final int num;

    private final int suffix;

    public LoincId(String loinccode) throws MaformedLoincCodeException {
        int dash_pos=loinccode.indexOf("-");
        if (dash_pos<=0) throw new MaformedLoincCodeException("No dash found in "+loinccode);
        if (dash_pos >loinccode.length()-2)
            throw new MaformedLoincCodeException("No character found after dash in " + loinccode);
        try {
            num=Integer.parseInt(loinccode.substring(0,dash_pos));
            suffix=Integer.parseInt(loinccode.substring(dash_pos+1));
        } catch (NumberFormatException nfe) {
            throw new MaformedLoincCodeException("Unable to parse numerical part of "+ loinccode);
        }
    }



    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof LoincId)) {
            return false;
        }
        LoincId other = (LoincId) o;
        return (this.num==other.num && this.suffix==other.suffix);
    }


    @Override
    public int hashCode() {
        int result = 17;
        result += 31 * num;
        return result + 31*suffix;
    }

    @Override
    public String toString() { return String.format("%d-%d",num,suffix); }



}

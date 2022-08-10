package org.monarchinitiative.loinc2hpocore.model;

import java.util.Objects;

public class LoincId implements Comparable<LoincId> {
    /** The part of the Loinc code prior to the dash */
    private final int num;
    /** The part of the Loinc code following the dash */
    private final int suffix;

    public LoincId(String loinccode)  {
        this(loinccode, false);
    }

    public LoincId(String loinccode, boolean hasPrefix) {
        if (hasPrefix){
            loinccode = loinccode.split(":")[1];
        }
        int dash_pos=loinccode.indexOf("-");
        if (dash_pos<=0) throw Loinc2HpoRuntimeException.malformedLoincCode("No dash found in "+loinccode);
        if (dash_pos >loinccode.length()-2)
            throw Loinc2HpoRuntimeException.malformedLoincCode("No character found after dash in " + loinccode);
        try {
            num=Integer.parseInt(loinccode.substring(0,dash_pos));
            suffix=Integer.parseInt(loinccode.substring(dash_pos+1));
        } catch (NumberFormatException nfe) {
            throw Loinc2HpoRuntimeException.malformedLoincCode("Unable to parse numerical part of "+ loinccode);
        }
    }



    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof LoincId other)) {
            return false;
        }
        return (this.num==other.num && this.suffix==other.suffix);
    }


    @Override
    public int hashCode() {
        return Objects.hash(num, suffix);
    }

    @Override
    public String toString() { return String.format("%d-%d",num,suffix); }


    @Override
    public int compareTo(LoincId that) {
        if (this.num != that.num) {
            return Integer.compare(this.num, that.num);
        } else {
            return Integer.compare(this.suffix, that.suffix);
        }
    }
}

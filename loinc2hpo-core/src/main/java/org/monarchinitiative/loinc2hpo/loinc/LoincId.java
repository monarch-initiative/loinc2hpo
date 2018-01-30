package org.monarchinitiative.loinc2hpo.loinc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;

public class LoincId {
    private static final Logger logger = LogManager.getLogger();
    /** The part of the Loinc code prior to the dash */
    private final int num;
    /** The part of the Loinc code following the dash */
    private final int suffix;

    public LoincId(String loinccode) throws MalformedLoincCodeException {
        int dash_pos=loinccode.indexOf("-");
        if (dash_pos<=0) throw new MalformedLoincCodeException("No dash found in "+loinccode);
        if (dash_pos >loinccode.length()-2)
            throw new MalformedLoincCodeException("No character found after dash in " + loinccode);
        try {
            num=Integer.parseInt(loinccode.substring(0,dash_pos));
            suffix=Integer.parseInt(loinccode.substring(dash_pos+1));
        } catch (NumberFormatException nfe) {
            throw new MalformedLoincCodeException("Unable to parse numerical part of "+ loinccode);
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

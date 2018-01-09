package org.monarchinitiative.loinc2hpo.loinc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.exception.MaformedLoincCodeException;

public class LoincId {
    private static final Logger logger = LogManager.getLogger();
    private final int num;

    private final int suffix;

    public LoincId(String loinccode) throws MaformedLoincCodeException {
        int dash_pos=loinccode.indexOf("-");
        logger.trace(String.format("loinc id string=%s, length=%d, dashpos=%d",loinccode,loinccode.length(),dash_pos));
        if (dash_pos<=0) throw new MaformedLoincCodeException("No dash found in "+loinccode);
        if (dash_pos >loinccode.length()-2)
            throw new MaformedLoincCodeException("No character found after dash in " + loinccode);
        try {
            num=Integer.parseInt(loinccode.substring(0,dash_pos));
            logger.trace("num="+num);
            suffix=Integer.parseInt(loinccode.substring(dash_pos+1));
            logger.trace("suff="+suffix);
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

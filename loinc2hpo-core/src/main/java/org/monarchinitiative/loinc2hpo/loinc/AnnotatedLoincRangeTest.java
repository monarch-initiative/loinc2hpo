package org.monarchinitiative.loinc2hpo.loinc;

import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a key class of the library, and represents one annotated Loinc test, including three values: one if the
 * test result was below normal, within normal limits, or above normal. Still a prototype
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.1.2
 */
public class AnnotatedLoincRangeTest implements  AnnotatedLoincTestI {
    private static final Logger logger = LogManager.getLogger();

    private final String loincNumber;
    private final HpoTerm belowNormalTerm;
    private final HpoTerm notAbnormalTerm;
    private final HpoTerm aboveNormalTerm;
    private final String loincScale;
    private boolean flag;





    /**
     * ToDo implement me.
     * @param loincCode
     * @param value
     * @param unit
     * @return
     */
    public HpoTerm loincValueToHpo(String loincCode, String value, String unit) {
        return null;
    }




    public AnnotatedLoincRangeTest(String loinc, String loincScale, HpoTerm low, HpoTerm normal, HpoTerm hi, boolean flag){
        //allow low, normal, hi to be null
        this.loincNumber=loinc;
        this.loincScale = loincScale;
        this.belowNormalTerm=low;
        this.notAbnormalTerm=normal;
        this.aboveNormalTerm=hi;
        this.flag = flag;

        /**
        logger.trace(String.format("low: %s; normal: %s, high: %s",
                low.getName(),
                normal.getName(),
                hi.getName()));
         **/
        // todo more validation.

    }

    public void setFlag(boolean newflag){ this.flag = newflag;}

    public String getLoincNumber(){ return loincNumber; }
    public String getBelowNormalHpoTermName() { return this.belowNormalTerm==null ? null : belowNormalTerm.getName(); }
    public String getNotAbnormalHpoTermName() { return this.notAbnormalTerm==null ? null : notAbnormalTerm.getName(); }
    public String getAboveNormalHpoTermName() { return this.aboveNormalTerm==null ? null : aboveNormalTerm.getName(); }
    public String getLoincScale() { return loincScale;}
    public boolean getFlag(){ return flag;}


}

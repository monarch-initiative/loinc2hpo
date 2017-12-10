package org.monarchinitiative.loinc2hpo.loinc;

import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a key class of the library, and represents one annotated Loinc test, including three values: one if the
 * test result was below normal, within normal limits, or above normal. Still a prototype
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class AnnotatedLoincRangeTest implements  AnnotatedLoincTestI {
    private static final Logger logger = LogManager.getLogger();

    private final String loincNumber;
    private final Age lowAge;
    private final Age highAge;
    private final NormalRange range;
    private final HpoTerm belowNormalTerm;
    private final HpoTerm notAbnormalTerm;
    private final HpoTerm aboveNormalTerm;






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




    public AnnotatedLoincRangeTest(String loinc,HpoTerm low, HpoTerm normal, HpoTerm hi, Integer ageLoY, Integer ageLoM, Integer ageLoD,
                                   Integer ageHiY, Integer ageHiM, Integer ageHiD, String rangeLo, String rangeHi, String unit) {
        this.lowAge=new Age(ageLoY,  ageLoM,  ageLoD);
        this.highAge=new Age(ageHiY,  ageHiM,  ageHiD);
        this.range = new NormalRange(rangeLo,rangeHi,unit);
        this.loincNumber=loinc;
        this.belowNormalTerm=low;
        this.notAbnormalTerm=normal;
        this.aboveNormalTerm=hi;

        logger.trace(String.format("low: %s; normal: %s, high: %s; age-lo:%s, age-hi:%s, range:%s",
                low.getName(),
                normal.getName(),
                hi.getName(),
                lowAge.toString(),
                highAge.toString(),
                range.toString()));
        // todo more validation.

    }

    public String getLoincNumber(){ return loincNumber; }
    public String getBelowNormalHpoTermName() { return belowNormalTerm.getName(); }
    public String getNotAbnormalHpoTermName() { return notAbnormalTerm.getName(); }
    public String getAboveNormalHpoTermName() { return aboveNormalTerm.getName(); }
    public String getLowAgeBoundry() { return lowAge.toString(); }
    public String getHighAgeBoundry() { return highAge.toString();}
    public String getRangeAsString(){ return range.toString();}


}

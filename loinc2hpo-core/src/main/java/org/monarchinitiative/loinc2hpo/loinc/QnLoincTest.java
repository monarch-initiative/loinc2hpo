package org.monarchinitiative.loinc2hpo.loinc;

import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.ontology.data.TermId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a key class of the library, and represents one annotated Loinc test, including three values: one if the
 * test result was below normal, within normal limits, or above normal. Still a prototype
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.1.2
 */
public class QnLoincTest extends LoincTest {
    private static final Logger logger = LogManager.getLogger();


    private final HpoTermId4LoincTest belowNormalTerm;
    private final HpoTermId4LoincTest notAbnormalTerm;
    private final HpoTermId4LoincTest aboveNormalTerm;

    boolean flag=false;

    private String note; //what user wants to say about an annotation, e.g. "the hpo term is the best fit because ..."


    @Override
    public HpoTermId4LoincTest loincInterpretationToHpo(LoincObservationResult obs) {
        switch (obs.getCategory()) {
            case LOW: return belowNormalTerm;
            case HIGH: return aboveNormalTerm;
            case WITHIN_NORMAL_RANGE: return notAbnormalTerm;
            default: return null;
        }
    }

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




    public QnLoincTest(LoincId loinc, LoincScale loincScale, TermId low, TermId normal, TermId hi){
        super(loinc,loincScale);
        this.belowNormalTerm=new HpoTermId4LoincTest(low);
        boolean negated=true;
        this.notAbnormalTerm=new HpoTermId4LoincTest(normal,negated);
        this.aboveNormalTerm=new HpoTermId4LoincTest(hi);

        /**
        logger.trace(String.format("low: %s; normal: %s, high: %s",
                low.getName(),
                normal.getName(),
                hi.getName()));
         **/
        // todo more validation.

    }

    public QnLoincTest(LoincId loinc, LoincScale loincScale, TermId low, TermId normal, TermId hi, boolean fl, String note){
        this(loinc,loincScale,low,normal,hi);
        this.note=note;
        this.flag=fl;
    }


    @Override
    public TermId getBelowNormalHpoTermId() { return this.belowNormalTerm==null ? null : belowNormalTerm.getId(); }
    @Override
    public TermId getNotAbnormalHpoTermName() { return this.notAbnormalTerm==null ? null : notAbnormalTerm.getId(); }
    @Override
    public TermId getAboveNormalHpoTermName() { return this.aboveNormalTerm==null ? null : aboveNormalTerm.getId(); }

    @Override
    public TermId getCorrespondingHpoTermName() {
        return null;
    }

    @Override
    public String getNote() { return note;}

    @Override
    public boolean getFlag(){ return flag; }




}

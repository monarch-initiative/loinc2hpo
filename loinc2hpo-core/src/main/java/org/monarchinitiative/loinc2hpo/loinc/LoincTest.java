package org.monarchinitiative.loinc2hpo.loinc;

import com.github.phenomics.ontolib.ontology.data.TermId;

/**
 * This is a class for a Loinc test.
 */
public abstract class LoincTest {

    protected final LoincId id;

    protected final LoincScale scale;

    abstract public HpoTermId4LoincTest loincInterpretationToHpo(LoincObservationResult obs);


    public LoincTest(LoincId lid, LoincScale lsc) {
        id=lid;
        scale=lsc;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof LoincTest)) {
            return false;
        }
        LoincTest lt = (LoincTest) o;
        return this.id.equals(lt.id);
    }


    @Override
    public int hashCode() {
        int result = 17;
        return result + 31 * id.hashCode() + 11* scale.hashCode();
    }
    public LoincScale getLoincScale() { return this.scale;}
    public LoincId getLoincNumber(){ return this.id; }
    public abstract TermId getBelowNormalHpoTermId();
    public abstract TermId getNotAbnormalHpoTermName();
    public abstract TermId getAboveNormalHpoTermName();
    public abstract TermId getCorrespondingHpoTermName();
    public abstract String getNote();


    public abstract boolean getFlag();

}

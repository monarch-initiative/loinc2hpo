package org.monarchinitiative.loinc2hpo.loinc;

import com.github.phenomics.ontolib.formats.hpo.HpoTerm;

public abstract class LoincTest {

    protected final LoincId id;

    protected final LoincScale scale;

    abstract public Hpo2LoincTermId loincValueToHpo(LoincObservation obs);


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


}

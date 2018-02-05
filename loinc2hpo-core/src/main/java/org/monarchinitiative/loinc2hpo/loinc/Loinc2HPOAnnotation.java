package org.monarchinitiative.loinc2hpo.loinc;

import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.ontology.data.TermId;
import org.hl7.fhir.dstu3.model.Coding;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;

import java.util.HashMap;

/**
 * This is a class for a Loinc test.
 */
public abstract class Loinc2HPOAnnotation {

    protected LoincId id;
    protected LoincScale scale;

    public Loinc2HPOAnnotation(LoincId lid, LoincScale lsc) {
        id=lid;
        scale=lsc;
    }

    abstract public HpoTermId4LoincTest loincInterpretationToHpo(ObservationResultInInternalCode obs);
    //use this method to retrieve the hpo term for a particular result (coded with an internal code)
    abstract public HpoTermId4LoincTest loincInterpretationToHPO(Code code);

    public Loinc2HPOAnnotation setLoincId(LoincId loincId){
        this.id = loincId;
        return this;
    }
    public Loinc2HPOAnnotation setLoincScale(LoincScale scale){
        this.scale = scale;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Loinc2HPOAnnotation)) {
            return false;
        }
        Loinc2HPOAnnotation lt = (Loinc2HPOAnnotation) o;
        return this.id.equals(lt.id);
    }


    @Override
    public int hashCode() {
        int result = 17;
        return result + 31 * id.hashCode() + 11* scale.hashCode();
    }
    public LoincScale getLoincScale() { return this.scale;}
    public LoincId getLoincNumber(){ return this.id; }
    public abstract String getNote();
    //For the following five methods, the first five are for easy access
    //the fifth one can be used to deal with all cases
    public abstract TermId getBelowNormalHpoTermId();
    public abstract TermId getAbnormalHpoTermName();
    public abstract TermId getNotAbnormalHpoTermName();
    public abstract TermId getAboveNormalHpoTermName();
    //public abstract TermId getCorrespondingHpoTermName(String code);



    public abstract boolean getFlag();

}

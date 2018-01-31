package org.monarchinitiative.loinc2hpo.loinc;

import com.github.phenomics.ontolib.ontology.data.TermId;
import org.hl7.fhir.dstu3.model.Coding;

import java.util.HashMap;

/**
 * This is a class for a Loinc test.
 */
public abstract class Loinc2HPOAnnotation {

    protected LoincId id;
    protected LoincScale scale;

    public Loinc2HPOAnnotation(){

    }
    public Loinc2HPOAnnotation(LoincId lid, LoincScale lsc) {
        id=lid;
        scale=lsc;
    }

    abstract public HpoTermId4LoincTest loincInterpretationToHpo(ObservationResultInInternalCode obs);

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
    public abstract TermId getBelowNormalHpoTermId();
    public abstract TermId getNotAbnormalHpoTermName();
    public abstract TermId getAboveNormalHpoTermName();
    public abstract TermId getCorrespondingHpoTermName();



    public abstract boolean getFlag();

}

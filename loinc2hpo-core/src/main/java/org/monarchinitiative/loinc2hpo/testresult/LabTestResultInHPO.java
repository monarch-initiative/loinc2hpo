package org.monarchinitiative.loinc2hpo.testresult;


import ca.uhn.fhir.model.base.composite.BaseIdentifierDt;
import ca.uhn.fhir.model.base.composite.BaseResourceReferenceDt;
import org.hl7.fhir.dstu3.model.Observation;
import org.monarchinitiative.phenol.ontology.data.TermId;

public interface LabTestResultInHPO {

    TermId getTermId();

    boolean isNegated();

    Observation getObservation();

    BaseIdentifierDt getTestIdentifier();

    BaseResourceReferenceDt getSubjectReference();

}

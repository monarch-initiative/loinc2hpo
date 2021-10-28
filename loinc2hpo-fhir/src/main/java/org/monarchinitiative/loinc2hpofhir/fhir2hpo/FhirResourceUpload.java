package org.monarchinitiative.loinc2hpofhir.fhir2hpo;

import ca.uhn.fhir.rest.api.MethodOutcome;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IBaseResource;

public interface FhirResourceUpload {

    /**
     * Upload separated pieces of FHIR resources
     * @param resource
     * @return
     */
    MethodOutcome upload(IBaseResource resource);

    /**
     * Upload FHIR resources as a bundle.
     * It is helpful to upload newly created interrelated resources as a bundle as it allows referring each other with temporary Ids.
     * @param bundle
     * @return
     */
    Bundle upload(Bundle bundle);

}

package org.monarchinitiative.loinc2hpo.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.monarchinitiative.loinc2hpo.exception.AmbiguousSubjectException;
import org.monarchinitiative.loinc2hpo.exception.SubjectNotFoundException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FhirServerDstu3Impl implements FhirServer{

    private String base;
    private IGenericClient client;

    public FhirServerDstu3Impl(String s) {
        this.base = s;
        client = FhirContext.forDstu3().newRestfulGenericClient(this.base);
    }

    @Override
    public IGenericClient restifulGenericClient() {

        return this.client;
    }

    @Override
    public String getBaseAddress() {

        return this.base;
    }

    @Override
    public List<Patient> getPatient(Reference subject) {
        /**
        List<Patient> patients = new ArrayList<>();
        if (subject.hasReference()) {
            String ref = subject.getReference();
            if (!ref.startsWith(BASEURL) && ref.startsWith("Patient")) {
                ref = BASEURL + "/" + ref;
            }
            Bundle patientBundle = client.search().byUrl(ref).returnBundle(Bundle.class).execute();

            while (true) {
                patientBundle.getEntry()
                        .forEach(p -> patients.add((Patient) p.getResource()));
                if (patientBundle.getLink(IBaseBundle.LINK_NEXT) != null){
                    patientBundle = client.loadPage().next(patientBundle).execute();
                } else {
                    break;
                }
            }
        } else if (subject.hasIdentifier()) {
            Identifier identifier = subject.getIdentifier();
            //TODO: find patient through the identifier
        }
        if (patients.size() == 1) {
            return patients.iterator().next();
        } else if (patients.isEmpty()) {
            throw new SubjectNotFoundException("Expect one subject, but found none");
        } else {
            throw new AmbiguousSubjectException("Except one subject, but found multiple");
        }
         **/
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Patient> getPatient(Identifier identifier) {
        List<Patient> patientList = new ArrayList<>();
        Bundle patientBundle = client.search().forResource(Patient.class)
                .where(new TokenClientParam("identifier").exactly()
                        .systemAndCode(identifier.getSystem(), identifier.getValue()))
                .returnBundle(Bundle.class)
                .execute();

        while(true) {
            patientBundle.getEntry()
                    .forEach(p -> patientList.add((Patient) p.getResource()));
            if (patientBundle.getLink(IBaseBundle.LINK_NEXT ) != null) {
                patientBundle = client.loadPage().next(patientBundle).execute();
            } else {
                break;
            }
        }

        return patientList;
    }

    @Override
    public List<Patient> getPatient(String resourceId) {
        List<Patient> patientList = new ArrayList<>();
        Bundle patientBundle = client.search().forResource(Patient.class)
                .where(new TokenClientParam("_id").exactly().code(resourceId))
                .returnBundle(Bundle.class)
                .execute();

        while(true) {
            patientBundle.getEntry()
                    .forEach(p -> patientList.add((Patient) p.getResource()));
            if (patientBundle.getLink(IBaseBundle.LINK_NEXT ) != null) {
                patientBundle = client.loadPage().next(patientBundle).execute();
            } else {
                break;
            }
        }

        return patientList;
    }

    @Override
    public List<Patient> getPatient(String firstName, String lastName) {
        List<Patient> patientList = new ArrayList<>();
        Bundle patientBundle = client.search().forResource(Patient.class)
                .where(new StringClientParam("family").matches().value(lastName))
                .where(new StringClientParam("given").matches().value(firstName))
                .returnBundle(Bundle.class)
                .execute();

        while(true) {
            patientBundle.getEntry()
                    .forEach(p -> patientList.add((Patient) p.getResource()));
            if (patientBundle.getLink(IBaseBundle.LINK_NEXT ) != null) {
                patientBundle = client.loadPage().next(patientBundle).execute();
            } else {
                break;
            }
        }

        return patientList;
    }

    @Override
    public List<Patient> getPatient(String firstName, String lastName, String phoneOrEmail, String zipcode) {
        List<Patient> patientList = new ArrayList<>();
        Bundle patientBundle = client.search().forResource(Patient.class)
                .where(new StringClientParam("family").matches().value(lastName))
                .where(new StringClientParam("given").matches().value(firstName))
                .where(new StringClientParam("address-postalcode").matches().value(zipcode))
                .where(new TokenClientParam("telecom").exactly().code(phoneOrEmail))
                .returnBundle(Bundle.class)
                .execute();

        while(true) {
            patientBundle.getEntry()
                    .forEach(p -> patientList.add((Patient) p.getResource()));
            if (patientBundle.getLink(IBaseBundle.LINK_NEXT ) != null) {
                patientBundle = client.loadPage().next(patientBundle).execute();
            } else {
                break;
            }
        }

        return patientList;
    }

    @Override
    public List<Observation> getObservation(Patient patient) {
        List<Observation> observationList = new ArrayList<>();
        String id = patient.getIdElement().getIdPart();
        System.out.println("id for query: " + id);
        if (id != null) {
            Bundle observationBundle = client.search().forResource(Observation.class)
                    .where(new ReferenceClientParam("patient").hasId(id))
                    .prettyPrint()
                    .returnBundle(Bundle.class)
                    .execute();

            while(true) {
                observationBundle.getEntry()
                        .forEach(p -> observationList.add((Observation) p.getResource()));
                if (observationBundle.getLink(IBaseBundle.LINK_NEXT ) != null) {
                    observationBundle = client.loadPage().next(observationBundle).execute();
                } else {
                    break;
                }
            }
        }
        return observationList;
    }

    @Override
    public MethodOutcome upload(IBaseResource resource) {
        MethodOutcome outcome = client.create()
                .resource(resource)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome;
    }

    @Override
    public Bundle upload(Bundle bundle) {
        return client.transaction().withBundle(bundle).execute();
    }
}

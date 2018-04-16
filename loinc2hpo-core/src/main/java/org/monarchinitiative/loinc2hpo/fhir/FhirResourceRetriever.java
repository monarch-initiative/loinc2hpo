package org.monarchinitiative.loinc2hpo.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.monarchinitiative.loinc2hpo.exception.AmbiguousSubjectException;
import org.monarchinitiative.loinc2hpo.exception.SubjectNotFoundException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FhirResourceRetriever {


    static final String LOINC_SYSTEM = "http://loinc.org";
    //let user set the base url from the GUI
    //By default, we use hapi-fhir test server
    public static String BASEURL = "http://fhirtest.uhn.ca/baseDstu3";
    private static final Logger logger = LogManager.getLogger();
    public static final FhirContext ctx = FhirContext.forDstu3();
    static final IGenericClient client = ctx.newRestfulGenericClient(BASEURL);
    public static final IParser jsonParser = ctx.newJsonParser();



    /**
     * This function parses a json file stored locally to an hapi-fhir Observation object
     * @param filepath
     * @return
     */
    public static Observation parseJsonFile2Observation(String filepath) throws IOException, DataFormatException {
        Observation observation = null;

        File file = new File(filepath);
        byte[] bytes = new byte[(int)file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bytes);
        //logger.debug(new String(bytes));
        IBaseResource ibaseResource = jsonParser.parseResource(new String(bytes));
        if (ibaseResource instanceof Observation) {
            observation = (Observation) ibaseResource;
        } else {
            throw new DataFormatException();
        }
        fileInputStream.close();

        return observation;
    }

    public static Observation parseJsonFile2Observation(File file) throws IOException {

        return parseJsonFile2Observation(file.getAbsolutePath());

    }

    public static String toJsonString(Observation observation) {

        return jsonParser.setPrettyPrint(true).encodeResourceToString(observation);

    }

    public static String toJsonString(Patient patient) {
        return jsonParser.setPrettyPrint(true).encodeResourceToString(patient);
    }

    /**
     * @TODO: implement it
     * retrieve a patient's observations from FHIR server
     * @param patient
     * @return
     */
    public static List<Observation> retrieveObservationFromServer(Patient patient) {

        List<Observation> observationList = new ArrayList<>();
        String id = patient.getId();
        if (id != null) {
            Bundle observationBundle = client.search().forResource(Observation.class)
                    .where(new ReferenceClientParam("subject").hasId(id))
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

    /**
     * Retrieve a patient from the reference field of an observation
     * @param subject
     * @return
     */
    public static Patient retrievePatientFromServer(Reference subject) throws SubjectNotFoundException, AmbiguousSubjectException {

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

    }

    private List<Observation> toList(Bundle bundle) {

        List<Observation> resourceList = new ArrayList<Observation>();
        while (true) {
            bundle.getEntry()
                    .forEach(p -> resourceList.add((Observation) p.getResource()));
            if (bundle.getLink(IBaseBundle.LINK_NEXT) != null){
                bundle = client.loadPage().next(bundle).execute();
            } else {
                break;
            }
        }

        return resourceList;

    }

    public List<Observation> retrieveBeautyObservationFromServer() {

        List<Observation> observationList = new ArrayList<>();
        //String id = patient.getId();
        //if (id != null) {
        Bundle observationBundle = client.search().forResource(Observation.class)
                //.where(new ReferenceClientParam("subject").hasId())
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
        //}
        return observationList;

    }

}


package org.monarchinitiative.loinc2hpo;

public class Constants {

    //missing values are noted as "NA" during serialization
    public static final String MISSINGVALUE = "NA";

    //folder and file names for serializations
    //1. TSVSingleFile format
    public static final String TSVSingleFileFolder = "TSVSingleFile";
    public static final String TSVSingleFileName = "annotations.tsv";
    //2. TSVSeperateFiles format
    public static final String TSVSeparateFilesFolder = "TSVSeparateFiles";
    public static final String TSVSeparateFilesBasic = "basic_annotations.tsv";
    public static final String TSVSeparateFilesAdv = "advanced_annotations.tsv";
    //3. JSON format
    public static final String JSONFileFolder = "JSON";
    public static final String JSONfile = "annotations.json";

    //folder for LOINC categories
    public static final String LOINCCategory = "LOINC CATEGORY";

    //folder for Data folder
    public static final String DATAFOLDER = "Data";


    public static final String LOINCSYSTEM = "http://loinc.org";
    //public static final String HAPIFHIRTESTSERVER = "http://fhirtest.uhn.ca/baseDstu3";
    public static final String HAPIFHIRTESTSERVER = "http://hapi.fhir.org/baseDstu3";
    public static final String UNITSYSTEM = "http://unitsofmeasure.org";

    public static final String V2OBSERVATIONINTERPRETATION = "http://hl7.org/fhir/v2/0078";
    public static final String V3OBSERVATIONINTERPRETATION = "http://hl7.org/fhir/v3/ObservationInterpretation";



}

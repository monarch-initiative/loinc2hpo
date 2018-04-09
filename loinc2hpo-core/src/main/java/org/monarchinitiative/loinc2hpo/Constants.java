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



}

package org.monarchinitiative.loinc2hpo.command;

import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;

import java.util.HashMap;
import java.util.Map;

public class DemoCommand extends  Command   {

    Map<String,UniversalLoinc2HPOAnnotation> testMap;




    public DemoCommand(String downloaddir) {

        testMap=new HashMap<>();
    }


    public void execute() {
        ClassLoader classLoader = DemoCommand.class.getClassLoader();
        String loincpath = classLoader.getResource("loinctest2hpo.csv").getFile();
        //ImmutableMap<String,Loinc2HPOAnnotation> loncmap = Loinc2HPOAnnotation.getLoincTestMap(loincpath);
    }



}

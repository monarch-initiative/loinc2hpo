package org.monarchinitiative.loinc2hpo.command;

import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.loinc2hpo.loinc.LoincTest;

import java.util.HashMap;
import java.util.Map;

public class DemoCommand extends  Command   {

    Map<String,LoincTest> testMap;




    public DemoCommand(String downloaddir) {

        testMap=new HashMap<>();
    }


    public void execute() {
        ClassLoader classLoader = DemoCommand.class.getClassLoader();
        String loincpath = classLoader.getResource("loinctest2hpo.csv").getFile();
        //ImmutableMap<String,LoincTest> loncmap = LoincTest.getLoincTestMap(loincpath);
    }



}

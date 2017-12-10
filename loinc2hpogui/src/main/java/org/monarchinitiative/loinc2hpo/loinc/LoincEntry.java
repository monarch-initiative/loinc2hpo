package org.monarchinitiative.loinc2hpo.loinc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.util.LoincImporter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class LoincEntry {
    private static final Logger logger = LogManager.getLogger();

    private String LOINC_Number=null;

    private String component=null;

    private String property=null;

    private String timeAspect=null;

    private static final int MIN_FIELDS_LOINC=4;



    public LoincEntry(String line) throws Exception {
        List<String> F = LoincImporter.splitCSVquoted(line);
        if (F.size()<MIN_FIELDS_LOINC) {
            throw new Exception("malformed LOINC line: "+line);
        }
        LOINC_Number=F.get(0);
        component=F.get(1);
        property=F.get(2);
        timeAspect=F.get(3);
//        for (int i=0;i<F.size();i++) {
//            System.out.println(i + ") "+ F.get(i));
//        }
    }


    public String getLOINC_Number(){ return LOINC_Number;}
    public String getComponent() { return component; }
    public String getProperty() { return property; }
    public String getTimeAspect() { return timeAspect; }

    public static ImmutableMap<String,LoincEntry> getLoincEntryList(String pathToLoincCoreTable) {
        ImmutableMap.Builder<String,LoincEntry> builder = new ImmutableMap.Builder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(pathToLoincCoreTable));
            String line=null;
            int c=0;
            String header=br.readLine();
            if (! header.contains("\"LOINC_NUM\"")) {
                logger.error(String.format("Malformed header line (%s) in Loinc File %s",header,pathToLoincCoreTable));
                return builder.build(); // empty list
            }
            while ((line=br.readLine())!=null) {
                try {
                    LoincEntry entry = new LoincEntry(line);
                    builder.put(entry.getLOINC_Number(),entry);
                } catch (Exception e) {
                    logger.error("Could not construct LOINC entry");
                    e.printStackTrace();
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return builder.build();

    }


}

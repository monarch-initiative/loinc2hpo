package org.monarchinitiative.loinc2hpo.loinc;

import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoincTest {
    private static final Logger logger = LogManager.getLogger();

    private String loincNum;
    private Unit unit;


    public void LoincTest(String line ) {
        String[] A = line.split("\t");
        if (A.length<7) {
            logger.error(String.format("Malformed line with %d instead of 7 fields\n\t%s",A.length,line));
            return;
        }
        loincNum=A[0];

    }





    public static ImmutableMap<String,LoincTest> getLoincTestMap(String path) {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line =null;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("LOINC_NUM"))
                    continue; // header line
                System.out.println(line);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }



        return builder.build();
    }


}

package org.monarchinitiative.loinc2hpocore.loinc;

import com.google.common.collect.ImmutableMap;

import org.monarchinitiative.loinc2hpocore.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpocore.sparql.LoincImporter;
import org.monarchinitiative.loinc2hpocore.sparql.LoincLongNameComponents;
import org.monarchinitiative.loinc2hpocore.sparql.LoincLongNameParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class LoincEntry {
    private static final Logger logger = LoggerFactory.getLogger(LoincEntry.class);

    private final LoincId LOINC_Number;

    private final String component;

    private final String property;

    private final String timeAspect;

    private final String system;

    private final String scale;

    private final String method;

    private final String longName;

    //parse long name into single components
    private final LoincLongNameComponents longNameComponents;

    private static final int MIN_FIELDS_LOINC=10;

    private static final String HEADER_LINE="FLAG\t#LOINC.id\tLOINC.scale\tHPO.low\tHPO.wnl\tHPO.high\tnote";



    public LoincEntry(String line) throws MalformedLoincCodeException {
        List<String> F = LoincImporter.splitCSVquoted(line);
        if (F.size()<MIN_FIELDS_LOINC) {
            throw new MalformedLoincCodeException("malformed LOINC line: "+line);
        }
        LOINC_Number= new LoincId(F.get(0));
        component=F.get(1);
        property=F.get(2);
        timeAspect=F.get(3);
        system=F.get(4);
        scale=F.get(5);
        method=F.get(6);
        longName=F.get(9);

        this.longNameComponents = LoincLongNameParser.parse(longName);

    }


    public LoincId getLOINC_Number(){ return LOINC_Number;}
    public String getComponent() { return component; }
    public String getProperty() { return property; }
    public String getTimeAspect() { return timeAspect; }
    public String getMethod() { return method; }
    public String getScale() { return scale; }
    public String getSystem() { return system; }
    public String getLongName() { return longName; }
    public LoincLongNameComponents getLongNameComponents() {
        return this.longNameComponents;
    }

    /**
     * Method to check that a LOINC is Ord and the outcome is either "Presence" or "Absence"
     * @return true if the LOINC is "Ord" and the outcome is either "Presence" or "Absence"
     */
    public boolean isPresentOrd() {
        return this.longNameComponents.getLoincType().startsWith("Presen");
    }


    public static ImmutableMap<LoincId,LoincEntry> getLoincEntryMap(String pathToLoincCoreTable) {
        ImmutableMap.Builder<LoincId,LoincEntry> builder = new ImmutableMap.Builder<>();
        int count_malformed = 0;
        int count_correct = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(pathToLoincCoreTable))){
            String line;
            String header=br.readLine();
            if (! header.contains("\"LOINC_NUM\"")) {
                logger.error(String.format("Malformed header line (%s) in Loinc File %s",header,pathToLoincCoreTable));
                return builder.build(); // empty list
            }
            while ((line=br.readLine())!=null) {
                try {
                    LoincEntry entry = new LoincEntry(line);
                    builder.put(entry.getLOINC_Number(),entry);
                    count_correct++;
                } catch (MalformedLoincCodeException e) {
                    logger.error("Malformed loinc code in the line:\n " + line);
                    count_malformed++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info(count_correct+ " loinc entries are created");
        logger.warn(count_malformed + " loinc numbers are malformed");

        return builder.build();

    }

    @Override
    public boolean equals(Object obj){
        if (this.LOINC_Number != null && obj instanceof LoincEntry) {
            LoincEntry other = (LoincEntry) obj;
            return this.LOINC_Number.equals(other.getLOINC_Number());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.LOINC_Number.hashCode();
    }


}

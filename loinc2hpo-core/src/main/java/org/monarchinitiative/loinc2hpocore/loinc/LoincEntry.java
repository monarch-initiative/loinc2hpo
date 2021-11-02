package org.monarchinitiative.loinc2hpocore.loinc;

import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LoincEntry {
    private static final Logger logger = LoggerFactory.getLogger(LoincEntry.class);

    private final LoincId loincId;

    private final String component;

    private final String property;

    private final String timeAspect;

    private final String system;

    private final LoincScale scale;

    private final String method;

    private final LoincLongName loincLongName;

    private static final int MIN_FIELDS_LOINC=10;

    private static final String HEADER_LINE="FLAG\t#LOINC.id\tLOINC.scale\tHPO.low\tHPO.wnl\tHPO.high\tnote";


    public LoincEntry(LoincId loincId, String comp, String property, String timeAspect, String system,
                      LoincScale scale, String method, LoincLongName longName) {
        this.loincId = loincId;
        this.component = comp;
        this.property = property;
        this.timeAspect = timeAspect;
        this.system = system;
        this.scale = scale;
        this.method = method;
        this.loincLongName = longName;
    }


    public LoincId getLoincId(){ return loincId;}
    public String getComponent() { return component; }
    public String getProperty() { return property; }
    public String getTimeAspect() { return timeAspect; }
    public String getMethod() { return method; }
    public LoincScale getScale() { return scale; }
    public String getSystem() { return system; }
    public String getLongName() { return loincLongName.getName(); }
    public LoincLongName getLoincLongName() {
        return this.loincLongName;
    }

    /**
     * Method to check that a LOINC is Ord and the outcome is either "Presence" or "Absence"
     * @return true if the LOINC is "Ord" and the outcome is either "Presence" or "Absence"
     */
    public boolean isPresentOrd() {
        return this.loincLongName.getLoincType().startsWith("Presen");
    }

    @Override
    public boolean equals(Object obj){
        if (this.loincId != null && obj instanceof LoincEntry) {
            LoincEntry other = (LoincEntry) obj;
            return this.loincId.equals(other.getLoincId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.loincId.hashCode();
    }


    /**
     * Structure of file:
     * "LOINC_NUM",
     * "COMPONENT",
     * "PROPERTY",
     * "TIME_ASPCT",
     * "SYSTEM",
     * "SCALE_TYP",
     * "METHOD_TYP","
     * CLASS",
     * "CLASSTYPE","
     * LONG_COMMON_NAME",
     * "SHORTNAME",
     * "EXTERNAL_COPYRIGHT_NOTICE",
     * "STATUS","
     * VersionFirstReleased",
     * "VersionLastChanged"
     * @param line e.g., "10000-8","R wave duration.lead AVR","Time","Pt","Heart","Qn","EKG","EKG.MEAS","2","R wave duration in lead AVR","R wave dur L-AVR","","ACTIVE","1.0i","2.48"
     * @return corresponding line
     */
    public static LoincEntry fromQuotedCsvLine(String line) {
        String [] fields = line.split(",");
        if (fields.length <MIN_FIELDS_LOINC) {
                throw Loinc2HpoRuntimeException.malformedLoincCode(line);
        }
        List<String> fieldsWithNoQuotes = Arrays.stream(fields)
                .map(w -> w.replaceAll("\"", ""))
                .collect(Collectors.toList());
        LoincId loincId = new LoincId(fieldsWithNoQuotes.get(0));
        String component = fieldsWithNoQuotes.get(1);
        String property = fieldsWithNoQuotes.get(2);
        String timeAspect = fieldsWithNoQuotes.get(3);
        String system = fieldsWithNoQuotes.get(4);
        LoincScale scale = LoincScale.fromString(fieldsWithNoQuotes.get(5));
        String method = fieldsWithNoQuotes.get(6);
        String longName = fieldsWithNoQuotes.get(9);
        return new LoincEntry(loincId, component, property, timeAspect, system, scale, method, LoincLongName.of(longName));
    }




}

package org.monarchinitiative.loinc2hpocore.loinc;

import org.monarchinitiative.loinc2hpocore.annotationmodel.LoincScale;
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

    public final static String [] headerFields = {
            "LOINC_NUM", "COMPONENT", "PROPERTY", "TIME_ASPCT", "SYSTEM",
            "SCALE_TYP", "METHOD_TYP","CLASS", "CLASSTYPE","LONG_COMMON_NAME",
            "SHORTNAME",
            "EXTERNAL_COPYRIGHT_NOTICE",
            "STATUS","VersionFirstReleased", "VersionLastChanged"};
    public final static String header = Arrays.stream(headerFields)
            .map(w -> String.format("\"%s\"",w))
            .collect(Collectors.joining(","));

    private final static int LOINC_ID_FIELD = 0;
    private final static int COMPONENT_FIELD = 1;
    private final static int PROPERTY_FIELD = 2;
    private final static int TIMEASPECT_FIELD = 3;
    private final static int SYSTEM_FIELD = 4;
    private final static int SCALETYP_FIELD = 5;
    private final static int METHOTYP_FIELD = 6;
    private final static int LONG_COMMON_NAME_FIELD = 9;






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
     * Structure of file: see {@link #headerFields}
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
        LoincId loincId = new LoincId(fieldsWithNoQuotes.get(LOINC_ID_FIELD));
        String component = fieldsWithNoQuotes.get(COMPONENT_FIELD);
        String property = fieldsWithNoQuotes.get(PROPERTY_FIELD);
        String timeAspect = fieldsWithNoQuotes.get(TIMEASPECT_FIELD);
        String system = fieldsWithNoQuotes.get(SYSTEM_FIELD);
        LoincScale scale = LoincScale.fromString(fieldsWithNoQuotes.get(SCALETYP_FIELD));
        String method = fieldsWithNoQuotes.get(METHOTYP_FIELD);
        String longName = fieldsWithNoQuotes.get(LONG_COMMON_NAME_FIELD);
        return new LoincEntry(loincId, component, property, timeAspect, system, scale, method, LoincLongName.of(longName));
    }




}

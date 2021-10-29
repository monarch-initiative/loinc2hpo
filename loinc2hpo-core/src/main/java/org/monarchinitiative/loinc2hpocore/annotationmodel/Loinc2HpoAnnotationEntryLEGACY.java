package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents one annotation in the {@code loinc2hpo-annotations.tsv} file that is available
 *  * at https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation
 */
public class Loinc2HpoAnnotationEntryLEGACY {

    private final String loincId;
    private final String loincScale;
    private final String system;
    private final ShortCode code;
    private final String hpoTermId;
    private final String isNegated;
    private final String createdOn;
    private final String createdBy;
    private final String lastEditedOn;
    private final String lastEditedBy;
    private final String version;
    private final String isFinalized;
    private final String comment;


    public Loinc2HpoAnnotationEntryLEGACY(String loincId, String loincScale, String system, ShortCode code, String hpoTermId, String isNegated, String createdOn, String createdBy, String lastEditedOn, String lastEditedBy, String version, String isFinalized, String comment) {
        this.loincId = loincId;
        this.loincScale = loincScale;
        this.system = system;
        this.code = code;
        this.hpoTermId = hpoTermId;
        this.isNegated = isNegated;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.lastEditedOn = lastEditedOn;
        this.lastEditedBy = lastEditedBy;
        this.version = version;
        this.isFinalized = isFinalized;
        this.comment = comment;
    }

    public static Loinc2HpoAnnotationEntryLEGACY fromTsvLine(String line) {
        String[] elements = line.split("\t");
        if (elements.length != 13){
            throw new RuntimeException("Line does not have expected length: " + line);
        }
        for (int i = 0; i < elements.length; i++){
            elements[i] = elements[i].equals("NA")? null : elements[i];
        }
        String loincId = elements[0];
        String loincScale = elements[1];
        String system = elements[2];
        ShortCode code = ShortCode.fromShortCode(elements[3]);
        String hpoTermId = elements[4];
        String isNegated = elements[5];
        String createdOn = elements[6];
        String createdBy = elements[7];
        String lastEditedOn = elements[8];
        String lastEditedBy = elements[9];
        String version = elements[10];
        String isFinalized = elements[11];
        String comment = elements[12];
        return new Loinc2HpoAnnotationEntryLEGACY(loincId, loincScale, system, code, hpoTermId, isNegated,
                createdOn, createdBy, lastEditedOn, lastEditedBy, version, isFinalized, comment);
    }
    public String getLoincId() {
        return loincId;
    }

    public String getLoincScale() {
        return loincScale;
    }

    public String getSystem() {
        return system;
    }

    public ShortCode getCode() {
        return code;
    }

    public String getHpoTermId() {
        return hpoTermId;
    }

    public String getIsNegated() {
        return isNegated;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getLastEditedOn() {
        return lastEditedOn;
    }

    public String getLastEditedBy() {
        return lastEditedBy;
    }

    public String getVersion() {
        return version;
    }

    public String getIsFinalized() {
        return isFinalized;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString(){
        List<String> fields = Arrays.asList(loincId, loincScale, system, code.shortForm(),
                hpoTermId, isNegated, createdOn, createdBy, lastEditedOn,
                lastEditedBy, version, isFinalized, comment);
        //replace any null value or empty value with "NA"
        List<String> replaceNullWithNA =
                fields.stream().map(f -> f == null || f.equals("")?
                "NA" : f).collect(Collectors.toList());
        return String.join("\t", replaceNullWithNA);
    }


    public static Loinc2HpoAnnotationEntryLEGACY of(String loincId,
                                                    String loincScale,
                                                    String system,
                                                    ShortCode code,
                                                    String hpoTermId,
                                                    String isNegated,
                                                    String createdOn,
                                                    String createdBy,
                                                    String lastEditedOn,
                                                    String lastEditedBy,
                                                    String version,
                                                    String isFinalized,
                                                    String comment)  {
        return new Loinc2HpoAnnotationEntryLEGACY(loincId, loincScale, system, code, hpoTermId, isNegated, createdOn, createdBy, lastEditedOn, lastEditedBy, version, isFinalized, comment);
    }
}

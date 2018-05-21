package org.monarchinitiative.loinc2hpo.io;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.exception.MalformedLoincCodeException;
import org.monarchinitiative.loinc2hpo.loinc.HpoTerm4TestOutcome;
import org.monarchinitiative.loinc2hpo.loinc.LOINC2HpoAnnotationImpl;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincScale;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoincAnnotationSerializerToTSVSingleFile implements LoincAnnotationSerializer {
    final String header = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
            "loincId", "loincScale", "system", "code",
            "hpoTermId", "isNegated", "createdOn", "createdBy",
            "lastEditedOn", "lastEditedBy", "version", "isFinalized",
            "comment");
    final String MISSINGVALUE = "NA";

    private Map<TermId, Term> hpoTermMap = null;

    private static final Logger logger = LogManager.getLogger();

    private LoincAnnotationSerializerToTSVSingleFile() {

    }

    public LoincAnnotationSerializerToTSVSingleFile(Map<TermId, Term> hpoTermMap) {

        this.hpoTermMap = hpoTermMap;

    }

    @Override
    public void serialize(Map<LoincId, LOINC2HpoAnnotationImpl> annotationmap, String filepath) throws IOException {



        BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
        writer.write(header);
        for (LOINC2HpoAnnotationImpl p : annotationmap.values()) {
            writer.write("\n");
            writer.write(annotationToString(p));
        }

        writer.close();

    }

    @Override
    public Map<LoincId, LOINC2HpoAnnotationImpl> parse(String filepath) throws FileNotFoundException {

        if (hpoTermMap == null) {
            throw new NullPointerException("hpoTermMap is not provided yet");
        }

        Map<LoincId, LOINC2HpoAnnotationImpl> deserializedMap = new LinkedHashMap<>();
        Map<LoincId, LOINC2HpoAnnotationImpl.Builder> builders = new LinkedHashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        reader.lines().forEach(serialized -> {
            String[] elements = serialized.split("\\t");
            if (elements.length == 13 && !serialized.startsWith("loincId")) {
                try {
                    LoincId loincId = new LoincId(elements[0]);
                    LoincScale loincScale = LoincScale.string2enum(elements[1]);
                    String system = elements[2];
                    String code = elements[3];
                    TermId termId = WriteToFile.convertToTermID(elements[4]);
                    boolean inverse = Boolean.parseBoolean(elements[5]);
                    LocalDateTime createdOn = elements[6].equals(MISSINGVALUE) ?
                            null : LocalDateTime.parse(elements[6]);
                    String createdBy = elements[7].equals(MISSINGVALUE)?
                            null : elements[7];
                    LocalDateTime lastEditedOn = elements[8].equals(MISSINGVALUE)?
                            null : LocalDateTime.parse(elements[8]);
                    String lastEditedBy = elements[9].equals(MISSINGVALUE) ?
                            null : elements[9];
                    double version = Double.parseDouble(elements[10]);
                    boolean flag;
                    try {
                        flag = ! Boolean.parseBoolean(elements[11]);
                    } catch (Exception e) {
                        flag = false;
                    }

                    String comment = elements[12].equals(MISSINGVALUE) ?
                            null : elements[12];

                    if (!builders.containsKey(loincId)) {
                        builders.put(loincId, new LOINC2HpoAnnotationImpl.Builder());
                        builders.get(loincId)
                                .setLoincId(loincId)
                                .setLoincScale(loincScale)
                                .setCreatedOn(createdOn)
                                .setCreatedBy(createdBy)
                                .setLastEditedOn(lastEditedOn)
                                .setLastEditedBy(lastEditedBy)
                                .setVersion(version)
                                .setFlag(flag)
                                .setNote(comment);
                    }

                    Code coding;
                    if (CodeSystemConvertor.getCodeContainer().getCodeSystemMap().containsKey(system)
                            && CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(system).containsKey(code)) {
                        coding = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(system).get(code);
                    } else {
                        coding = Code.getNewCode().setCode(code).setSystem(system);
                    }
                    HpoTerm4TestOutcome annotate = new HpoTerm4TestOutcome(hpoTermMap.get(termId), inverse);
                    builders.get(loincId).addAdvancedAnnotation(coding, annotate);
                } catch (MalformedLoincCodeException e) {
                    logger.error("Malformed loinc code line: " + serialized);
                }
            } else {
                if (elements.length != 13) {
                    logger.error(String.format("line does not have 13 elements, but has %d elements. Line: %s",
                            elements.length,  serialized));
                } else {
                    logger.info("line is header: " + serialized);
                }

            }
        });

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        builders.entrySet().forEach(p -> deserializedMap.put(p.getKey(), p.getValue().build()));
 //deserializedMap.values().forEach(System.out::println);
        return deserializedMap;
    }


    private String annotationToString(LOINC2HpoAnnotationImpl annotation) {
        StringBuilder builder = new StringBuilder();
        Map<String, Code> internalCode = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);


        //first put basic annotations there
        //annotation.getCandidateHpoTerms().keySet().forEach(System.out::println);
        annotation.getCandidateHpoTerms().entrySet()
                .stream()
                .filter(p -> p.getKey().getSystem().equals(Loinc2HPOCodedValue.CODESYSTEM))
                //"A" is the same as "N" except that they have opposite negation, so no need to save both
                .filter(p -> !p.getKey().getCode().equals("A"))
                .forEach(p -> {
                    builder.append(annotation.getLoincId());
                    builder.append("\t");
                    builder.append(annotation.getLoincScale());
                    builder.append("\t");
                    builder.append(p.getKey().getSystem());
                    builder.append("\t");
                    builder.append(p.getKey().getCode());
                    builder.append("\t");
                    builder.append(p.getValue().getHpoTerm().getId().getIdWithPrefix());
                    builder.append("\t");
                    builder.append(p.getValue().isNegated());
                    builder.append("\t");
                    builder.append(annotation.hasCreatedOn() ? annotation.getCreatedOn() : MISSINGVALUE);
                    builder.append("\t");
                    builder.append(annotation.hasCreatedBy() ? annotation.getCreatedBy() : MISSINGVALUE);
                    builder.append("\t");
                    builder.append(annotation.hasLastEditedOn() ?
                            annotation.getLastEditedOn() : MISSINGVALUE);
                    builder.append("\t");
                    builder.append(annotation.hasLastEditedBy() ?
                            annotation.getLastEditedBy() : MISSINGVALUE);
                    builder.append("\t");
                    builder.append(String.format("%.1f", annotation.getVersion()));
                    builder.append("\t");
                    builder.append(!annotation.getFlag());
                    builder.append("\t");
                    builder.append(annotation.hasComment() ? annotation.getNote() : MISSINGVALUE);
                    builder.append("\n");
                });

        //then process advanced annotations

        annotation.getCandidateHpoTerms().entrySet()
                .stream()
                .filter(p -> !p.getKey().getSystem().equals(Loinc2HPOCodedValue.CODESYSTEM))
                .forEach(p -> {
                    builder.append(annotation.getLoincId());
                    builder.append("\t");
                    builder.append(annotation.getLoincScale());
                    builder.append("\t");
                    builder.append(p.getKey().getSystem());
                    builder.append("\t");
                    builder.append(p.getKey().getCode());
                    builder.append("\t");
                    builder.append(p.getValue().getHpoTerm().getId().getIdWithPrefix());
                    builder.append("\t");
                    builder.append(p.getValue().isNegated());
                    builder.append("\t");
                    builder.append(annotation.hasCreatedOn() ? annotation.getCreatedOn() : MISSINGVALUE);
                    builder.append("\t");
                    builder.append(annotation.hasCreatedBy() ? annotation.getCreatedBy() : MISSINGVALUE);
                    builder.append("\t");
                    builder.append(annotation.hasLastEditedOn() ?
                            annotation.getLastEditedOn() : MISSINGVALUE);
                    builder.append("\t");
                    builder.append(annotation.hasLastEditedBy() ?
                            annotation.getLastEditedBy() : MISSINGVALUE);
                    builder.append("\t");
                    builder.append(annotation.getVersion());
                    builder.append("\t");
                    builder.append(!annotation.getFlag());
                    builder.append("\t");
                    builder.append(annotation.hasComment() ? annotation.getNote() : MISSINGVALUE);
                    builder.append("\n");
                });

        return builder.toString().trim();
    }

}

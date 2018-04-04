package org.monarchinitiative.loinc2hpo.io;

import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.ontology.data.TermId;
import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.loinc2hpo.codesystems.CodeSystemConvertor;
import org.monarchinitiative.loinc2hpo.codesystems.Loinc2HPOCodedValue;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.UniversalLoinc2HPOAnnotation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class LoincAnnotationSerializerToTSVSingleFile implements LoincAnnotationSerializer {
    final String header = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
            "loincId", "loincScale", "system", "code",
            "hpoTermId", "isNegated", "createdOn", "createdBy",
            "lastEditedOn", "lastEditedBy", "version", "isFinalized",
            "comment");
    final String MISSINGVALUE = "NA";

    private Map<TermId, HpoTerm> hpoTermMap = null;

    public LoincAnnotationSerializerToTSVSingleFile() {

    }

    public LoincAnnotationSerializerToTSVSingleFile(Map<TermId, HpoTerm> hpoTermMap) {

        this.hpoTermMap = hpoTermMap;

    }

    @Override
    public void serialize(Map<LoincId, UniversalLoinc2HPOAnnotation> annotationmap, String filepath) throws Exception {



        BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
        writer.write(header);
        for (UniversalLoinc2HPOAnnotation p : annotationmap.values()) {
            writer.write("\n");
            writer.write(annotationToString(p));
        }

        writer.close();

    }

    @Override
    public Map<LoincId, UniversalLoinc2HPOAnnotation> parse(String filepath) {

        if (hpoTermMap == null) {
            throw new NullPointerException("hpoTermMap is not provided yet");
        }



        return null;
    }


    private String annotationToString(UniversalLoinc2HPOAnnotation annotation) {
        StringBuilder builder = new StringBuilder();
        Map<String, Code> internalCode = CodeSystemConvertor.getCodeContainer().getCodeSystemMap().get(Loinc2HPOCodedValue.CODESYSTEM);

        //first put basic annotations there
        annotation.getCandidateHpoTerms().entrySet()
                .stream()
                .filter(p -> p.getKey().getSystem().equals(Loinc2HPOCodedValue.CODESYSTEM))
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

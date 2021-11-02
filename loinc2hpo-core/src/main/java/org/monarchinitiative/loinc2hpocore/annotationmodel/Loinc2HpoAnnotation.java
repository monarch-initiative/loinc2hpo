package org.monarchinitiative.loinc2hpocore.annotationmodel;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Optional;

public class Loinc2HpoAnnotation {

    private final TermId loincId;
    private final LoincScale loincScale;
    private final Outcome outcomeCode;
    private final TermId hpoTermId;
    private final String biocuration;
    private final Optional<TermId> supplementalOntologyTermId;
    private final String comment;

    private static final String LOINC_PREFIX = "LNC";

    public Loinc2HpoAnnotation(TermId loincId,
                               LoincScale loincScale,
                               Outcome code,
                               TermId hpoTermId,
                               TermId supplementalOntologyTermId,
                               String biocuration,
                               String comment) {
        this.loincId = loincId;
        this.loincScale = loincScale;
        this.outcomeCode = code;
        this.hpoTermId = hpoTermId;
        this.supplementalOntologyTermId = Optional.of(supplementalOntologyTermId);
        this.biocuration = biocuration;
        this.comment = comment;
    }

    public Loinc2HpoAnnotation(TermId loincId,
                               LoincScale loincScale,
                               Outcome code,
                               TermId hpoTermId,
                               String biocuration,
                               String comment) {
        this.loincId = loincId;
        this.loincScale = loincScale;
        this.outcomeCode = code;
        this.hpoTermId = hpoTermId;
        this.biocuration = biocuration;
        this.supplementalOntologyTermId = Optional.empty();
        this.comment = comment;
    }

    public TermId getLoincId() {
        return loincId;
    }

    public LoincScale getLoincScale() {
        return loincScale;
    }

    public Outcome getOutcome() {
        return outcomeCode;
    }

    public TermId getHpoTermId() {
        return hpoTermId;
    }

    public String getBiocuration() {
        return biocuration;
    }

    public Optional<TermId> getSupplementalOntologyTermId() {
        return supplementalOntologyTermId;
    }

    public String getComment() {
        return comment;
    }



    public static final String [] headerFields = {"loincId", "loincScale", "outcome", "hpoTermId",
            "supplementalTermId", "curation", "comment"};
    private static final int EXPECTED_NUMBER_OF_FIELDS = headerFields.length;


    public static Loinc2HpoAnnotation fromAnnotationLine(String line)  {
        String [] fields = line.split("\t");
        if (fields.length != EXPECTED_NUMBER_OF_FIELDS) {
            throw new Loinc2HpoRuntimeException(String.format("Malformed line with %d fields: %s", fields.length, line));
        }
        TermId loincId = TermId.of(LOINC_PREFIX, fields[0]);
        LoincScale scale = LoincScale.fromString(fields[1]);

        TermId hpoId = TermId.of(fields[3]);
        String curation = fields[5];
        String comment = fields[6];
        String outcomeString = fields[2];
        Outcome outcome;
        if (scale.equals(LoincScale.NOMINAL)) {
            outcome = Outcome.nominal(outcomeString);
        } else {
            outcome = new Outcome(ShortCode.fromShortCode(outcomeString));
        }
        if (fields[4].equals(".")) {
            return new Loinc2HpoAnnotation(loincId,scale,outcome,hpoId,curation,comment);
        }
        TermId supplementalId = TermId.of(fields[4]);
        return new Loinc2HpoAnnotation(loincId, scale, outcome, hpoId, supplementalId, curation,comment);
    }
}

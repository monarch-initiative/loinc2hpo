package org.monarchinitiative.loinc2hpocore.annotation;

import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
import org.monarchinitiative.loinc2hpocore.exception.Loinc2HpoRuntimeException;
import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Loinc2HpoAnnotation implements Comparable<Loinc2HpoAnnotation> {

    private final LoincId loincId;
    private final LoincScale loincScale;
    private final Outcome outcomeCode;
    private final TermId hpoTermId;
    private final String biocuration;
    private final Optional<TermId> supplementalOntologyTermId;
    private final String comment;

    private static final String LOINC_PREFIX = "LNC";

    public Loinc2HpoAnnotation(LoincId loincId,
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

    public Loinc2HpoAnnotation(LoincId loincId,
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

    public LoincId getLoincId() {
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


    public String toTsv() {
        String suppl = supplementalOntologyTermId.isPresent() ?
                supplementalOntologyTermId.get().getValue() : "";
        return String.format("%s\ts\t%s\t%s\t%s\t%s\t%s\t%s",
                loincId,
                loincScale,
                outcomeCode.getOutcome(),
                hpoTermId.getValue(),
                suppl,
                biocuration,
                comment
                );
    }


    public static Loinc2HpoAnnotation fromAnnotationLine(String line)  {
        String [] fields = line.split("\t");
        if (fields.length != EXPECTED_NUMBER_OF_FIELDS) {
            throw new Loinc2HpoRuntimeException(String.format("Malformed line with %d fields: %s", fields.length, line));
        }
        LoincId loincId = new LoincId(fields[0]);
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

    private static LoincScale getScale(List<Loinc2HpoAnnotation> outcomes) {
        List<LoincScale> scales = outcomes.stream()
                .map(Loinc2HpoAnnotation::getLoincScale)
                .distinct().collect(Collectors.toList());
        if (scales.size() == 0) {
            // should never happen
            throw new Loinc2HpoRuntimeException("Could not extract LoincScale");
        } else if (scales.size() > 1) {
            // should never happen
            throw new Loinc2HpoRuntimeException("Extracted more than one LoincScale");
        }
        return scales.get(0);
    }

    public static LoincAnnotation outcomes2LoincAnnotation(List<Loinc2HpoAnnotation> outcomes) {
        int n = outcomes.size();
        // map with all of the outcomes for the current LOINC test
        Map<Outcome, Loinc2HpoAnnotation> outcomeMap = outcomes.stream()
                .collect(Collectors.toMap(Loinc2HpoAnnotation::getOutcome, Function.identity()));
        LoincScale scale = getScale(outcomes);
        if (scale.equals(LoincScale.QUANTITATIVE)) {
            return QuantitativeLoincAnnotation.fromOutcomeMap(outcomeMap);
        } else if (scale.equals(LoincScale.ORDINAL)) {
            return OrdinalHpoAnnotation.fromOutcomeMap(outcomeMap);
        } else if (scale.equals(LoincScale.NOMINAL)) {
            return new NominalLoincAnnotation(outcomeMap);
        }
         // if we get here, we did not match and there is some problem
        for (var oc : outcomes) {
            System.err.println("[ERROR] " + oc);
        }
        StringBuilder sb = new StringBuilder("Malformed outcomes\nn=").append(outcomes.size());
        for (var oc : outcomes) {
            sb.append("\t[ERROR] " + oc + "\n");
        }
        throw new Loinc2HpoRuntimeException(sb.toString());


    }

    @Override
    public int compareTo(Loinc2HpoAnnotation that) {
        return this.loincId.compareTo(that.loincId);
    }

    @Override
    public String toString() {
        return toTsv();
    }
}

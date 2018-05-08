package org.monarchinitiative.loinc2hpo.loinc;

import org.monarchinitiative.loinc2hpo.codesystems.Code;
import org.monarchinitiative.phenol.formats.hpo.HpoTerm;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.time.LocalDateTime;
import java.util.*;

public interface LOINC2HpoAnnotation {


    LoincId getLoincId();


    LoincScale getLoincScale();


    String getComment();


    boolean getFlag();


    double getVersion();


    LocalDateTime getCreatedOn();


    String getCreatedBy();


    LocalDateTime getLastEditedOn();


    String getLastEditedBy();


    Map<Code, HpoTerm4TestOutcome> getAdvancedAnnotationTerms();


    void addAdvancedAnnotation(Code code, HpoTerm4TestOutcome hpoTerm4TestOutcome);


    TermId getBelowNormalHpoTermId();

    TermId getNotAbnormalHpoTermName();

    TermId getAbnormalHpoTermName();

    TermId getAboveNormalHpoTermName();

    TermId getNegativeHpoTermName();

    TermId getPositiveHpoTermName();

    HpoTerm whenValueLow();

    HpoTerm whenValueNormalOrNegative();

    boolean isNormalOrNegativeInversed();

    HpoTerm whenValueHighOrPositive();

    boolean hasCreatedOn();

    boolean hasCreatedBy();

    boolean hasLastEditedOn();

    boolean hasLastEditedBy();

    boolean hasComment();

    Set<Code> getCodes();


    /**
     * Get the corresponding Hpo term for a coded value
     * @param code a code in a coding system. Usually, it is the internal code; for Ord, Nom, or Nar, it can be codes of
     *             an external coding system
     * @return the hpo term wrapped in the HpoTerm4TestOutcome class
     */
    HpoTerm4TestOutcome loincInterpretationToHPO(Code code);

    HashMap<Code, HpoTerm4TestOutcome> getCandidateHpoTerms();

}

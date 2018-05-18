package org.monarchinitiative.loinc2hpo.testresult;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

public class PhenoSetTimeLineImpl implements PhenoSetTimeLine {

    private List<PhenoSetComponent> phenosetTimeLine;
    private PhenoSet phenoset;

    public PhenoSetTimeLineImpl(PhenoSet phenoset) {
        this.phenoset = phenoset;
        this.phenosetTimeLine = new LinkedList<>();
    }

    @Override
    public PhenoSet phenoset() {
        return this.phenoset;
    }

    @Override
    public List<PhenoSetComponent> getTimeLine() {
        return new LinkedList<>(this.phenosetTimeLine);
    }

    @Override
    public void insert(PhenoSetComponent newAbnorm) {
        if (this.phenosetTimeLine.isEmpty()) {
            phenosetTimeLine.add(newAbnorm);
            return;
        }
        for (int i = 0; i < phenosetTimeLine.size(); i++) {
            PhenoSetComponent current = phenosetTimeLine.get(i);
            if (current.effectiveStart().after(newAbnorm.effectiveStart())) {
                newAbnorm.changeEffectiveEnd(current.effectiveStart());
                phenosetTimeLine.add(i, newAbnorm);
                break;
            }
            if (current.effectiveEnd().after(newAbnorm.effectiveStart())){
                current.changeEffectiveEnd(newAbnorm.effectiveStart());
                if (i != phenosetTimeLine.size() - 1) {
                    newAbnorm.changeEffectiveEnd(phenosetTimeLine.get(i + 1).effectiveStart());
                }
                phenosetTimeLine.add(i + 1, newAbnorm);
                break;
            }
            //else, continue
        }

        messageTimeLine();

        return;

    }

    /**
     * If consecutive components are the same HPO term and negation form, combine them together
     */
    private void messageTimeLine() {
        //@TODO: implement

    }


    @Override
    public void delete(PhenoSetComponent phenoSetComponent) {
        throw new UnsupportedOperationException();

    }

    @Override
    public PhenoSetComponent current(Date date) {
        return null;
    }
}

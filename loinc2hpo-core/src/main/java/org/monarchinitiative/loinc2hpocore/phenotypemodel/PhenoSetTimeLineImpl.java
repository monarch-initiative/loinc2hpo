package org.monarchinitiative.loinc2hpocore.phenotypemodel;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PhenoSetTimeLineImpl implements PhenoSetTimeLine {

    private List<PhenotypeComponent> phenosetTimeLine;
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
    public List<PhenotypeComponent> getTimeLine() {
        return new LinkedList<>(this.phenosetTimeLine);
    }

    @Override
    public void insert(PhenotypeComponent newAbnorm) {

        phenoset.add(newAbnorm.abnormality());

        if (this.phenosetTimeLine.isEmpty()) {
            phenosetTimeLine.add(newAbnorm);
            return;
        }
        for (int i = 0; i < phenosetTimeLine.size(); i++) {
            PhenotypeComponent current = phenosetTimeLine.get(i);
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
    public PhenotypeComponent current(Date date) {
        for (PhenotypeComponent current : this.phenosetTimeLine) {
            if (current.isEffective(date)) {
                return current;
            }
        }
        return null;
    }

    @Override
    public PhenotypeComponent persistDuring(Date start, Date end) {
        for (PhenotypeComponent component : this.phenosetTimeLine) {
            if (component.isPersistingDuring(start, end)) {
                return component;
            }
        }
        return null;
    }

    @Override
    public List<PhenotypeComponent> occurredDuring(Date start, Date end) {
        return this.phenosetTimeLine.stream()
                .filter(p -> p.occurredDuring(start, end))
                .collect(Collectors.toList());
    }


}

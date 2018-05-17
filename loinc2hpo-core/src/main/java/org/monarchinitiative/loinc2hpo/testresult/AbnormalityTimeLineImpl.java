package org.monarchinitiative.loinc2hpo.testresult;

import java.sql.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AbnormalityTimeLineImpl implements AbnormalityTimeLine{

    private List<AbnormalityComponent> phenosetTimeLine;
    private AbnormalitySynonet phenoset;

    public AbnormalityTimeLineImpl(AbnormalitySynonet phenoset) {
        this.phenoset = phenoset;
        this.phenosetTimeLine = new LinkedList<>();
    }

    @Override
    public void insert(AbnormalityComponent newAbnorm) {
        if (this.phenosetTimeLine.isEmpty()) {
            phenosetTimeLine.add(newAbnorm);
            return;
        }
        for (int i = 0; i < phenosetTimeLine.size(); i++) {
            AbnormalityComponent current = phenosetTimeLine.get(i);
            if (current.effectiveStart().after(newAbnorm.effectiveStart())) {
                newAbnorm.changeEffectiveEnd(current.effectiveStart());
                phenosetTimeLine.add(i, newAbnorm);
                return;
            }
            if (current.effectiveEnd().after(newAbnorm.effectiveStart())){
                current.changeEffectiveEnd(newAbnorm.effectiveStart());
                phenosetTimeLine.add(i + 1, newAbnorm);
                return;
            }
            //else, continue
        }

    }

    private AbnormalityComponent occurredEarly(AbnormalityComponent a1, AbnormalityComponent a2) {
        if (a1.effectiveStart().before(a2.effectiveStart())) {
            return a1;
        } else {
            return a2;
        }
    }
    @Override
    public void delete(AbnormalityComponent abnormalityComponent) {
        throw new UnsupportedOperationException();

    }

    @Override
    public AbnormalityComponent current(Date date) {
        return null;
    }
}

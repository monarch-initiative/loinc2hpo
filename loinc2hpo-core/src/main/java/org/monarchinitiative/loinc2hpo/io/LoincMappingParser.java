package org.monarchinitiative.loinc2hpo.io;


import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermPrefix;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.github.phenomics.ontolib.ontology.data.TermPrefix;
import org.monarchinitiative.loinc2hpo.exception.Loinc2HpoException;
import org.monarchinitiative.loinc2hpo.exception.MalformedHpoTermIdException;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincScale;
import org.monarchinitiative.loinc2hpo.loinc.LoincTest;
import org.monarchinitiative.loinc2hpo.loinc.QnLoincTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * A clas to parse the biocurated Loinc-to-HPO file.
 * Current format
 * <pre>
 *     #FLAG	LOINC.id	LOINC.scale	HPO.low	HPO.wnl	HPO.high	note
 N	15074-8 Qn  HP:0001943  HP:0011015  HP:0003074  blood glucose test
 * </pre>
 */
public class LoincMappingParser {



    private final HpoOntology ontology;

    private static final TermPrefix HPPREFIX = new ImmutableTermPrefix("HP");

    private Set<LoincTest> testset;

    private Set<QnLoincTest> qntests;



    public LoincMappingParser(String loincPath, HpoOntology hpo) {
        this.ontology=hpo;
        testset=new HashSet<>();
        qntests=new HashSet<>();
    }


    public Set<LoincTest> getTests() { return testset; };

    public Set<QnLoincTest> getQnTests() { return qntests; }


    private void parseLoinc2Hpo(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#")) continue; // headr or comment
                String A[] = line.split("\t");
                String flag=A[0];
                try {
                    LoincId id = new LoincId(A[1]);
                    LoincScale loincScale=getScale(A[2]);
                    TermId low = getHpoTermId(A[3]);
                    TermId wnl = getHpoTermId(A[4]);
                    TermId high = getHpoTermId(A[5]);
                    if (loincScale.equals(LoincScale.Qn)) {
                        LoincTest test = new QnLoincTest(id,LoincScale.Qn,low,wnl,high);
                        testset.add(test);
                        qntests.add(new QnLoincTest(id,LoincScale.Qn,low,wnl,high));
                    } else {

                    }

                } catch (Loinc2HpoException e) {
                    e.printStackTrace();
                    continue;
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    LoincScale getScale(String sc) {
        switch (sc) {
            case "Qn": return LoincScale.Qn;
            default: return LoincScale.Unknown;
        }
    }


    TermId getHpoTermId(String termString) throws MalformedHpoTermIdException {
        int i = termString.indexOf(":");
        if (i!=2) throw new MalformedHpoTermIdException("Malformed HPO String: " + termString);
        TermId id = new ImmutableTermId(HPPREFIX,termString.substring(3));
        if (ontology.getTermMap().containsKey(id)) return id;
        else
            throw new MalformedHpoTermIdException("Could not find id "+ termString);
    }


}

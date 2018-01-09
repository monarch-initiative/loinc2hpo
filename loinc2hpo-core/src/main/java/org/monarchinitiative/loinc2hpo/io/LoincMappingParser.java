package org.monarchinitiative.loinc2hpo.io;


import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermPrefix;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.github.phenomics.ontolib.ontology.data.TermPrefix;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.exception.Loinc2HpoException;
import org.monarchinitiative.loinc2hpo.exception.MalformedHpoTermIdException;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;
import org.monarchinitiative.loinc2hpo.loinc.LoincScale;
import org.monarchinitiative.loinc2hpo.loinc.LoincTest;
import org.monarchinitiative.loinc2hpo.loinc.QnLoincTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    private static final Logger logger = LogManager.getLogger();


    private final HpoOntology ontology;

    private static final TermPrefix HPPREFIX = new ImmutableTermPrefix("HP");

    private Set<LoincTest> testset;

    private Set<QnLoincTest> qntests;



    private Map<LoincId, LoincTest> testmap;



    public LoincMappingParser(String loincPath, HpoOntology hpo) {
        this.ontology=hpo;
        testset=new HashSet<>();
        qntests=new HashSet<>();
        testmap=new HashMap<>();
       // parseLoinc2Hpo(loincPath);
        parseName(loincPath);
    }


    public Set<LoincTest> getTests() { return testset; };

    public Set<QnLoincTest> getQnTests() { return qntests; }


    public Map<LoincId, LoincTest> getTestmap() { return testmap; }


    public Map<String,TermId> name2id=new HashMap<>();

    TermId name2id(String name) {
        if (name2id.size()==0) {
            // fill it
            for (TermId id : ontology.getTermMap().keySet()) {
                String nam = ontology.getTermMap().get(id).getName();
                name2id.put(nam,id);
            }
        }
        return name2id.get(name);

    }

    private void parseName(String path) {
        logger.trace("Parsing at " + path);
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line=br.readLine())!=null) {
                logger.trace("reading line: " +line);
                if (line.startsWith("#")) continue; // headr or comment
                String A[] = line.split("\t");
                String flag=A[0];
                boolean flagval=false;
                if (flag.startsWith("Y")) flagval=true;
                try {
                    LoincId id = new LoincId(A[1]);
                    LoincScale loincScale=getScale(A[2]);
                    TermId low = name2id(A[3]);
                    TermId wnl = name2id(A[4]);
                    TermId high = name2id(A[5]);
                    String note = A[6];
                    if (loincScale.equals(LoincScale.Qn)) {
                        LoincTest test = new QnLoincTest(id,LoincScale.Qn,low,wnl,high,flagval,note);
                        testset.add(test);
                        qntests.add(new QnLoincTest(id,LoincScale.Qn,low,wnl,high));
                        testmap.put(id,test);
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



    private void parseLoinc2Hpo(String path) {
        logger.trace("Parsing at " + path);
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line=br.readLine())!=null) {
                logger.trace("reading line: " +line);
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
                        testmap.put(id,test);
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

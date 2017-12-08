package org.monarchinitiative.loinc2hpo.io;

import java.io.File;

/**
 * This class stores the URLs for the RefSeq.txt.gz file for the Ensembl regulatory features GTF file for the
 * indicated species (mm9,mm10,hg19,hg38).
 * See <a href="http://www.ensembl.org/info/genome/funcgen/regulatory_build.html">http://www.ensembl.org/info/genome/funcgen/regulatory_build.html</a>
 * This class is intended to be used with {@link Downloader}
 *
 * @author Peter Robinson
 * @version 0.1.3 (2017-11-12)
 */
public class HpoOboDownloader {
    final private static String hg19="ftp://ftp.ensembl.org/pub/grch37/update/regulation/homo_sapiens/homo_sapiens.GRCh37.Regulatory_Build.regulatory_features.20161117.gff.gz";
    final private static String hg38 = "ftp://ftp.ensembl.org/pub/release-90/regulation/homo_sapiens/homo_sapiens.GRCh38.Regulatory_Build.regulatory_features.20161111.gff.gz";


    private String genome = null;

    /**
     * @param genome The name of the genome assembly, e.g., hg19, hg38, mm9,mm10.
     */
    public HpoOboDownloader(String genome) {
        this.genome = genome;
    }



    public boolean needToDownload(String localDirectory) {
        File f = new File(localDirectory + File.separator + "hp.obo");
        if(f.exists() && !f.isDirectory()) {
            return false;
        }
        return true;
    }

}

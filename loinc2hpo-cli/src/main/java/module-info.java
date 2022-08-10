module monarchinitiative.loinc2hpo.cli {

    requires monarchinitiative.loinc2hpocore;
    requires info.picocli;
    requires org.monarchinitiative.phenol.core;
    requires org.monarchinitiative.phenol.io;
    requires freemarker;
    requires java.sql;

    opens org.monarchinitiative.loinc2hpocli.command  to info.picocli;
    opens org.monarchinitiative.loinc2hpocli.html to freemarker;
}
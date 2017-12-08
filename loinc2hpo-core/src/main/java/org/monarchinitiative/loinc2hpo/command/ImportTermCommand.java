package org.monarchinitiative.loinc2hpo.command;

import org.monarchinitiative.loinc2hpo.util.LoincImporter;

public class ImportTermCommand extends Command {

    private final String loincTablePath;

    private final String loincTermId;

    public ImportTermCommand(String loincTable, String termId) {
        loincTablePath=loincTable;
        loincTermId=termId;
    }

    public void execute() {
        LoincImporter importer = new LoincImporter(loincTablePath,loincTermId);

    }


    @Override
    public String toString() {
        return "import-term";
    }

}

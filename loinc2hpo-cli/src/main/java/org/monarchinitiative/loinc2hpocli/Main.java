package org.monarchinitiative.loinc2hpocli;

import org.monarchinitiative.loinc2hpocli.command.AnnotationQcCommand;
import org.monarchinitiative.loinc2hpocli.command.HtmlOutputCommand;
import org.monarchinitiative.loinc2hpocli.command.LoincTableCoreStatsCommand;

import picocli.CommandLine;


@CommandLine.Command(name = "loinc2hpo-cli builder", version = "0.0.1", mixinStandardHelpOptions = true)
public class Main implements Runnable {

    public static void main(String[] args) {
        if (args.length == 0) {
            // if the user doesn't pass any command or option, add -h to show help
            args = new String[]{"-h"};
        }
        CommandLine cline = new CommandLine(new Main())
                .addSubcommand("annotation-qc", new AnnotationQcCommand())
                .addSubcommand("html", new HtmlOutputCommand())
                .addSubcommand("stats", new LoincTableCoreStatsCommand());
        cline.setToggleBooleanFlags(false);
        int exitCode = cline.execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        // work done in subcommands
    }

}

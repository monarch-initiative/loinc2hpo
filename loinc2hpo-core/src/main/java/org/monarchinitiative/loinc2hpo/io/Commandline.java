package org.monarchinitiative.loinc2hpo.io;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.command.Command;
import org.monarchinitiative.loinc2hpo.command.DemoCommand;
import org.monarchinitiative.loinc2hpo.command.DownloadCommand;
import org.monarchinitiative.loinc2hpo.command.ImportTermCommand;


import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.Collectors;


public class Commandline {
    private static final Logger logger = LogManager.getLogger();
    private Command command=null;

    private final static String DEFAULT_DIGEST_FILE_NAME="hicupCloneDigest.txt";

    private final static String DEFAULT_TRUNCATION_SUFFIX="truncated";



    private final static String DEFAULT_DOWNLOAD_DIR="data";


    private String loincTablePath=null;
    private String loincTermId=null;
    private String dataDownloadDirectory=null;


    public Commandline(String args[]) {
        final CommandLineParser cmdLineGnuParser = new DefaultParser();

        final Options gnuOptions = constructGnuOptions();
        CommandLine commandLine;

        String mycommand = null;
        String clstring="";
        if (args!=null && args.length>0) {
            clstring= Arrays.stream(args).collect(Collectors.joining(" "));
        }
        try
        {
            commandLine = cmdLineGnuParser.parse(gnuOptions, args);
            String category[] = commandLine.getArgs();
            if (category.length != 1) {
                printUsage("command missing");
            } else {
                mycommand=category[0];

            }
            if (commandLine.getArgs().length<1) {
                printUsage("no arguments passed");
                return;
            }
            if (commandLine.hasOption("t")) {
                this.loincTermId=commandLine.getOptionValue("t");
            }
            if (commandLine.hasOption("d")) {
            this.dataDownloadDirectory=commandLine.getOptionValue("d");
        }
            if (commandLine.hasOption("L")) {
                this.loincTablePath=commandLine.getOptionValue("L");
            }
        }
        catch (ParseException parseException)  // checked exception
        {
            String msg = String.format("Could not parse options %s [%s]",clstring, parseException.toString());
           printUsage(msg );
        }
        try {
            if (mycommand.equals("import-term")) {
                if (this.loincTermId == null) {
                    printUsage("-t option required for import-term command");
                }
                if (this.loincTablePath==null) {
                    printUsage("-L option required for import-term command");
                }

                this.command = new ImportTermCommand(this.loincTablePath, loincTermId);

            } else if (mycommand.equals("download")) {
                if (this.dataDownloadDirectory == null) {
                    this.dataDownloadDirectory=DEFAULT_DOWNLOAD_DIR;
                }
                logger.trace(String.format("Download command to %s",dataDownloadDirectory));
                this.command=new DownloadCommand(dataDownloadDirectory);
            } else if (mycommand.equals("demo")) {
                if (this.dataDownloadDirectory == null) {
                    this.dataDownloadDirectory=DEFAULT_DOWNLOAD_DIR;
                }
                this.command=new DemoCommand(dataDownloadDirectory);
            }   else
            {
                printUsage(String.format("Did not recognize command: %s", mycommand));
            }
        } catch (Exception de) {
            de.printStackTrace();
            printUsage(de.getMessage());
        }
    }


    public Command getCommand() {
        return command;
    }

    /**
     * Construct and provide GNU-compatible Options.
     *
     * @return Options expected from command-line of GNU form.
     */
    public static Options constructGnuOptions()
    {
        final Options options = new Options();
        options.addOption("o", "out", true, "name/path of output file/directory")
                .addOption("t", "termid", true, "LOINC term id")
                .addOption("L", "loinc-table", true, "path to LOINC Core Table file");
        return options;
    }



    /**
     * Print usage information to provided OutputStream.
     */
    public static void printUsage(String message)
    {
        final PrintWriter writer = new PrintWriter(System.out);
        final HelpFormatter usageFormatter = new HelpFormatter();
        final String applicationName="java -jar org.monarchinitiative.loinc2hpo.jar command";
        final Options options=constructGnuOptions();
        writer.println(message);
        usageFormatter.printUsage(writer, 120, applicationName, options);
        writer.println("\twhere command is one of demo,download,,....");
        writer.println("\t- demo: show an example....");
        writer.println("\t- download [-d downloaddir] download hpo ontology file.");
        writer.close();
        System.exit(0);
    }

}

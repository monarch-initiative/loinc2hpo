package org.monarchinitiative.loinc2hpocore.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This is a class to run command line from the app
 */
public class TerminalCommand {

    private String[] commands;
    private String dir;

    /**
     * Instantiate the class with a list of commandline elements and the directory where the commands run in
     * @param commands
     * @param dir if dir does not exist, throw an exception
     */
    public TerminalCommand(String[] commands, String dir) {
        this.commands = commands;
        this.dir = dir;
        if (!Files.exists(Paths.get(this.dir))) {
            throw new IllegalArgumentException("dir not found");
        }
    }

    public int execute() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(this.commands);
        pb.directory(new File(this.dir));
        Process p = pb.start();
        p.waitFor();
        //p.getInputStream();
        //p.getErrorStream();
        //p.getOutputStream();
        return p.exitValue();
    }




}

package org.monarchinitiative.loinc2hpo.io;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore
public class TerminalCommandTest {
    @Test
    public void execute() throws Exception {

        String[] commands = new String[] {"/bin/bash", "-c", "say Jackson Laboratory"};
        System.out.println(System.getProperty("user.home"));
        TerminalCommand tm = new TerminalCommand(commands, System.getProperty("user.home"));
        int exitvalue = tm.execute();
        if (exitvalue == 0) {
            System.out.println("success");
        } else {
            System.out.println("Fail");
        }

    }

    @Test
    public void execute2() throws Exception {

        String[] commands = new String[] {"/bin/bash", "-c", "wc -l foo"};
        System.out.println(System.getProperty("user.home"));
        TerminalCommand tm = new TerminalCommand(commands, System.getProperty("user.home"));
        int exitvalue = tm.execute();
        if (exitvalue == 0) {
            System.out.println("success");
        } else {
            System.out.println("Fail");
        }

    }

    @Test
    public void execute3() throws Exception {

        String[] commands = new String[] {"/bin/bash", "-c", "echo \"Biocurator: JGM:Aaron\" | mail -s \"LOCKING loinc2hpoAnnotation\" \"kingmanzhang@gmail.com\""};
        System.out.println(System.getProperty("user.home"));
        TerminalCommand tm = new TerminalCommand(commands, System.getProperty("user.home"));
        int exitvalue = tm.execute();
        if (exitvalue == 0) {
            System.out.println("success");
        } else {
            System.out.println("Fail");
        }

    }

}
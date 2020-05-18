package org.monarchinitiative.loinc2hpo.gui;

import org.monarchinitiative.loinc2hpo.Main;

import java.io.File;
import java.util.ArrayList;

/**
 * This is a class to restart an app. Basic idea: build another process to run the app before exiting the current
 * running one. It only works when the app is run from a jar file.
 * https://stackoverflow.com/questions/4159802/how-can-i-restart-a-java-application
 */
public class RestartApp {

    public static void restartApplication() throws Exception {

        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

  /* is it a jar file? */
        if(!currentJar.getName().endsWith(".jar")){
            return;
        }


  /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);
    }
}

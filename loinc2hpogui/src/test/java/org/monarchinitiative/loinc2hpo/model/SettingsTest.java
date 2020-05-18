package org.monarchinitiative.loinc2hpo.model;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class SettingsTest {
    private Settings settings;
    private String settingsPath = "/Users/zhangx/.loinc2hpo/loinc2hpo.settings";

    @Test
    public void loadSettings() throws Exception {
        Settings.loadSettings(settings, settingsPath);
        System.out.println(settings.getAnnotationFolder());
        System.out.println(settings.getBiocuratorID());
        System.out.println(settings.getHpoOboPath());
        System.out.println(settings.getHpoOwlPath());
        System.out.println(settings.getLoincCoreTablePath());
        System.out.println(settings.getUserCreatedLoincListsColor());

        System.out.println(System.getProperty("user.home"));
    }

    @Test
    public void writeSettings() throws Exception {

    }

}
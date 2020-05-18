package org.monarchinitiative.loinc2hpo.model;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.gui.Loinc2HpoPlatform;

import javax.inject.Singleton;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@Disabled
public class AppResourcesTest {

    @Inject
    static Injector injector = Guice.createInjector();

    @Singleton
    static ResourceCollection resourceCollection = injector.getInstance(ResourceCollection.class);

    @Singleton
    static Settings settings = injector.getInstance(Settings.class);

    static AppResources appResources;

    @BeforeAll
    public static void setup() throws Exception {
        String settingsPath = Loinc2HpoPlatform.getPathToSettingsFile();
        Settings.loadSettings(settings, settingsPath);
        System.out.println(settings);
        appResources = injector.getInstance(AppResources.class);
        appResources.init();
    }

    @Test
    public void getHpo() throws Exception {
        assertNotNull(appResources.getHpo());
    }

    @Test
    public void getTermidTermMap() throws Exception {
        assertNotNull(appResources.getTermidTermMap());
    }

    @Test
    public void getTermnameTermMap() throws Exception {
        assertNotNull(appResources.getTermnameTermMap());
    }

    @Test
    public void getLoincEntryMap() throws Exception {
        assertNotNull(appResources.getLoincEntryMap());
    }

    @Test
    public void getLoincEntryMapFromName() throws Exception {
        assertNotNull(appResources.getLoincEntryMapFromName());
    }

    @Test
    public void getLoincAnnotationMap() throws Exception {
        assertNotNull(appResources.getLoincAnnotationMap());
    }

}
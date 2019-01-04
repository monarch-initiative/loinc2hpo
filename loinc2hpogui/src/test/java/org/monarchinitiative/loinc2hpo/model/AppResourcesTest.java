package org.monarchinitiative.loinc2hpo.model;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.loinc2hpo.ResourceCollection;
import org.monarchinitiative.loinc2hpo.gui.Loinc2HpoPlatform;

import static org.junit.Assert.*;

@Ignore
public class AppResourcesTest {

    @Inject
    static Injector injector = Guice.createInjector();

    static ResourceCollection resourceCollection = injector.getInstance(ResourceCollection.class);

    static Settings settings = injector.getInstance(Settings.class);

    static AppResources appResources;

    @BeforeClass
    public static void setup() throws Exception {
        String settingsPath = Loinc2HpoPlatform.getPathToSettingsFile();
        Settings.loadSettings(settings, settingsPath);
        appResources = injector.getInstance(AppResources.class);
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
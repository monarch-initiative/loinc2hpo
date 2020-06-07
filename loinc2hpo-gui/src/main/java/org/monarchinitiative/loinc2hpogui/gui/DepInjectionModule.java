package org.monarchinitiative.loinc2hpogui.gui;


import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.monarchinitiative.loinc2hpogui.ResourceCollection;
import org.monarchinitiative.loinc2hpogui.model.AppResources;
import org.monarchinitiative.loinc2hpogui.model.AppTempData;
import org.monarchinitiative.loinc2hpogui.model.Settings;

public class DepInjectionModule extends AbstractModule {
    @Override
    protected void configure() {

    }

    @Provides @Singleton
    Settings provideSettings() {
        return new Settings();
    }

    @Provides @Singleton
    ResourceCollection provideResourceCollection() {
        return new ResourceCollection();
    }

    @Provides @Singleton
    AppTempData provideModel() {
        return new AppTempData();
    }

    @Provides @Singleton
    AppResources provideAppResources(ResourceCollection resourceCollection, Settings settings) {
        AppResources appResources = new AppResources(resourceCollection, settings);
        appResources.init();
        return appResources;
    }

}
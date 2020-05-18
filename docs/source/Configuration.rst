Configuration
=============

The app requires the following configurations the first time you run it.

Mandatory Settings
------------------

* Download the Loinc Core Table from `loinc.org <https://loinc.org/downloads/loinc/>`_. Follow instructions from the loinc.org website. You need to register before you can download the document. The current version is *Loinc Version 2.63* (2017/12/15 release).

* Configure the path to the Loinc core table. From the menu bar, click **"Configuration"** - **"Set path to Loinc Core Table file"** and point to the LoincTableCore.csv file.

* Download HPO file. From the menu bar, click **"Configuration"** - **"Download HPO file"**. The files (HPO in .obo and .owl formats) will be automatically downloaded.

* Set the path to auto-saved data. First clone the repository for loinc2hpoAnnotation from Github (https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation)[https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation]. Then from the menu bar, click **"Configuration"** - **"Set path to Auto-saved Data"**, point to the loinc2hpoAnnotation folder.

**Restart the app to apply all settings**


Optional Settings
-----------------
The following setting are strongly recommended. Not specifying them will not
affect the operation of the app. 

* Set biocurator ID. From the menu bar, click **"Configuration"** - **"Set biocurator ID"**, specify your biocurator ID. If you are not assigned one, create one for yourself with the following format: organization name first followed by `:`, then your name/id.

* Once you are done, click **"Configuration"** - **"Show settings"** to view all your settings. The first two settings should **NOT** be null in order for the app to work correctly.

* Use customized hpo. If you prefer to use your own versions of hpo, click **"Configuration"** - **"Change hpo.owl"** to set the path to your hpo.owl file; use the button below to set the path to the hpo.obo file. An important note: inconsistencies of hpo.owl and hpo.obo files will lead to errors, so make sure your hpo OWL and OBO files are serialized from the same HPO. For details, refer to https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation/blob/master/README.md#annotating-with-newly-created-hpo-terms


Change Settings
---------------
The settings will be saved to a local file so that you do not need to repeat the above steps every time you run the app. This applies even after you upgrade to a newer version of the app. Should you decide to change the settings, follow the above steps accordingly to overwrite the original settings.


Configuration
=============

The app requires the following configurations the first time you run it.

Mandatory Settings
------------------

* Download the Loinc Core Table from `loinc.org <https://loinc.org/downloads/loinc/>`_. Follow instructions from the loinc.org website. You need to register before you can download the document. The current version is *Loinc Version 2.63* (2017/12/15 release).

* Configure the path to the Loinc core table. From the menu bar, click **"Configuration"** - **"Set path to Loinc Core Table file"** and point to the Loinc Core Table file downloaded from last step.

* Download HPO file. From the menu bar, click **"Configuration"** - **"Download HPO file"**. The files (HPO in .obo and .owl formats) will be automatically downloaded.

**Restart the app to apply all settings**


Optional Settings
-----------------
The following setting are recommended. Not specifying them will not
affect the operation of the app. 

* Change the directory for auto-saved data. The default directory for auto-saved data is located at ~/.loinc2hpo/Data. If you want to change this setting,  from the menu bar, click **Configuration** - **Set path to Autosaved Data** to change the directory for autosaved data.

  note: this step is mandatory if you want to use and push your annotation to `loinc2hpoAnnotation <https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation>`_. Follow instructions there to set up the path properly.

* Set biocurator ID. From the menu bar, click **"Configuration"** - **"Set biocurator ID"**, specify your biocurator ID. If you are not assigned one, create one for yourself with the following format: organization name first followed by `:`, then your name/id.

* Once you are done, click **"Edit"** - **"Show settings"** to view all your settings. The first two settings should **NOT** be null in order for the app to work correctly.

Change Settings
---------------
The settings will be saved to a local file so that you do not need to repeat the above steps every time you run the app. Should you want to change the settings, follow the above steps accordingly to overwrite the original settings.


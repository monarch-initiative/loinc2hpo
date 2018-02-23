# org.monarchinitiative.loinc2hpo
Java library to map LOINC-encoded test results to Human Phenotype Ontology
There are current two modules. loinc2hpogui is a JavaFX app that intends to help with biocuration of LOINC code to HPO term mappings. To build and run the GUI, use the following command.
```
$ mvn clean package
$ java -jar loinc2hpogui/target/Loinc2HpoGui.jar
```
To run the library code, enter the following
```
$ java -jar loinc2hpo-core/target/Loinc2HpoLib.jar download
```
This will download the hp.obo file (You need to manually download the LOINC Core Table file). There is a demo function that currently doesn't do anything
```
$ java -jar loinc2hpo-core/target/Loinc2HpoLib.jar demo
```


# Tutorial

## Configuration

Follow the steps below the first time you run the app.

  - Download the Loinc Core Table from [loinc.org](https://loinc.org/downloads/loinc/). Follow instructions from the loinc.org website. You may need to register before you are able to download the document. The current version is *Loinc Version 2.63* (2017/12/15 release).

  - Configure the path to the Loinc core table. From the menu bar, click **"Edit"** - **"Set path to Loinc Core Table file"** and point to the Loinc Core Table file downloaded from last step.

  - Download HPO file. From the menu bar, click **"Edit"** - **"Download HPO file"**. The files (HPO in .obo and .owl formats) will be automatically downloaded.

  - Set biocurator ID. From the menu bar, click **"Edit"** - **"Set biocurator ID"**, specify your biocurator ID. If you are not assigned one, create one for yourself with the following format: organization name first, then `:`, then your name/id.

  - Once you are done, click **"Edit"** - **"Show settings"** to view all your settings. The first two settings should **NOT** be null in order for the app to work correctly.

The settings will be saved to a local file so that you do not need to repeat the above steps every time you run the app. Should you want to change the settings, follow the above steps accordingly and your new setting will overwrite the old ones.

## Biocuration

The default tab, `Annotate`, is responsible to help the curation of loinc codes (another word to say it, map HPO terms to Loinc codes). Simply speaking, a Loinc code is a number that uniquely identifies a clinical lab test, e.g. Loinc 10449-7 means it is a test on glucose concentration in serum or plasma measured at 1 hour after meal. [Learn more about Loinc](https://loinc.org/learn/) A lab test is done for a patient by clinical labs and the result is transmitted to the physicians, who reads the result and interprets what it means for the patient. Our goal here is to assign appropriate HPO terms to Loinc codes when the corresponding test gives a above normal, normal, or below normal values. With the above Loinc `10449-7` (`"Glucose in Serum or Plasma -- 1 hour post meal"`) as an example, we will assign three HPO terms to this loinc, `Hyperglycemia` if the result is above normal, `hypoglycemia` if the result is below normal, and `Abnormality of blood glucose concentration` (but negated! -- equavalent to say that the test indicates the patient does NOT have `Abnormality of Blood glucose concentration`). In a real-world situation, we certainly need more tests (say, similar glucose test but measured 2 hours, or 3 hours after a meal) to call "hyperglycemia" or "hypoglycemia" for the patient. We will leave that to other functions of the app, so for the curation purpose we will think every test is powerful enough to diagnose the patient.

### Basic Mode curation

#### Quick Start
Follow the steps to start the curation process.

  - Import Loinc codes. Click **"Initialize Loinc Table"** on the left upper corner to import Loinc codes from the Loinc Core Table file. If you encounter an error, you may not have downloaded it or the path is not specified yet. Try using the "Search" function to select some Loinc codes, e.g. try searching for "10449-7" and then "glucose" (You will get one result for "10449-7" and many results for "glucose").

  - Import HPO. Click "Initialize HPO model" on the left upper corner to import all HPO terms to the app.

  - After completing the above steps, you should be able to start curating Loinc codes!
    - Go to the Loinc Table in the bottom half of the tab, and choose the Loinc code that you want to annotate. When you click the **"Auto Query"** button or double click on the Loinc code, the app will try to find candidate HPO terms by matching the HPO terms to the Loinc name. The more similar a HPO term matches the Loinc name, the higher score it will receive and the higher it sits in the table list.

    - Choose the appropriate term and drag it to one of the three textfields. (Tip: you can drag another term to overwrite the current one; try to avoid manual typing to avoid spelling error).

    - After you are done with the current annotation, you can click **"Create annotation"** button to record your annotations. Congratulations, you are done with one Loinc code! Now go to the `Loinc2HpoAnnotations` tab and your annotation will appear in the table.

#### A few helpful features

 - Filter the Loinc table. There are ~85,000 Loinc codes and you probably do not want to annotate all of them. You can supply a list of Loinc codes to your interest and only annotate those. To do this, create a .txt file with one Loinc code in each line (see the following example.) Save the file and click the **"Filter"** button. Point to the .txt file and you should get a much shorter list of Loinc codes in the table.
 ```
  15074-8
  779-9
  600-7
 ```

 - Flag an annotation. If you are not certain about your annotation and wants to come back later, you can check the **"Flag"** checkerbox as a reminder.

 - Leave a note for your annotation. You can leave a note on your annotation whenever necessary, e.g. why annotate this way, why it needs revisit, etc..

 - Find parent and child for an HPO term. When you double-click on a candidate HPO term, the app will find its parent(s) and child(ren) and display it in the right box. Sometimes this can be helpful in determining whether a HPO term should be chosen or not. (Tip: it can save time, too. e.g. If you double-click on "Abnormality of blood glucose concentration", you will find "Hyperglycemia" and "Hypoglycemia" as its two children. Drag the children to fill the correct textfields, without needing to go through the long list of candidate terms looking for "Hyperglycemia" and "Hypoglycemia"!)

 - Manually search for candidate HPO terms. If the **"Auto Query"** does not give you the HPO terms that you need, try using the **"Manual Query"** button with comma-separated keys. Tip: **_try synonyms_**; words without comma will be taken as one key and the app will try to find a exact match to it.


#### Review/Edit your annotations

You can review all your annotations (sort of...continue to `Advanced Mode curation` to see why) under the `Loinc2HpoAnnotations` tab. You can see the basic annotations for all the Loinc codes that you have annotated. You can right-click a Loinc annotation and click "Review" to open up a window that display all the annotations. Focus on the left two tables for the current being and we will deal with right table later.

Alternatively, you can also review annotations for one Loinc in the `Annotate` tab. Select the Loinc code first and then click the **All annotations** button.

Editing an existing Loinc annotation is as easy. You may go to `Loinc2HpoAnnotations` tab, right-click a Loinc code and choose "Edit", or choose "Edit" on the review window (right bottom). Overwrite your annotations with new values and click "Save" to save your new data.

### Advanced Mode curation

Consider this Loinc test: `Loinc 600-7` or `Bacteria identified in Blood by Culture`. The name of the Loinc code suggests two types of possible outcomes:

1. Whether there are bacteria identified in the blood or not (alternatively, we say whether the patient is "positive" or "negative" for bacterial infection). In this case, we choose *"Recurrent bacterial infections"* for positive and the same term in negated form for negative result and annotate in the Basic Mode as described above.

2. What type of bacteria is identified in the patient's blood. Here is from a real-world example:
```
"coding":[
{
  "system": "http://snomed.info/sct",
  "code": "3092008",
  "display": "Staphylococcus aureus"
}
 ]
```
We can guess that the above lines indicate that the patient has *S. aureus* infection in his/her blood. In this case, our Basic Mode does not work well anymore because it only handles values that are too high, too low and intermediate. This is when Advanced Mode comes along. To allow our app recognize this result, we need to assign a HPO term for **Snomed** code `3092008`. To do this,

  - Select Loinc `600-7` by using the "Search" function.
  - Annotate Loinc `600-7` at the Basic Mode as described in last section. You may also skip this step to next one.
  - Annotate at Advanced Mode. Click "advanced>>>" button and you will see three new textfields for **"system"**, **"code"**, and **"hpo term"**.
  - Type in *""http://snomed.info/sct""* into **"system"**, *"3092008"* into **"code"**. (Note: the information in **"system"** and **"code"** is sufficient to encode a piece of information, **"display"** is only used for display purposes so we do not need it)
  - Now we have to choose a HPO term. As an example, we double-click on *"Recurrent bacterial infections"* and drag one of its children *"Recurrent staphylococcal infections"* to the **"hpo term"** field. Click the `+` button to add this annotation.
  - Repeat the above two steps if you have more codes to add. After we are done, click `Create annotation` button to complete.
  - Now if you review your annotations for 600-7, you can see annotation data in the left bottom table. (This is why we said the table in `Loinc2HpoAnnotations` does not show all the annotations information--because it does not show data that were created for **Advanced Mode curation**)

  Note:
  Pay attention to the strict proprietary right of Snomed codes. It may not be allowed to map them to other codes.

### Term negation
Term negation means that you cannot find a HPO term that matches your need, but the opposite of a HPO term does. For example, if a patient's blood glucose concentration is normal, we say that the inverse of "Abnormality of blood glucose concentration" best describes his/her phenotype.

Note:
In the Basic Mode, the **"negate"** button only controls the term in the center textfield. The default value is `true` for Basic Mode, `false` for the Advanced Mode.


### Suggest new HPO terms

Sometimes you may not be able to find an appropriate HPO term for a Loinc code. You can request the authors of HPO to create new terms for you.

 - Create a new term for a Loinc code. Select a Loinc code and then click **"Suggest New HPO term"**. Provide the proposed term and your comment, type in your GitHub username and GitHub password, choose a label that best describes your request, e.g.
 > new term request
    and click **"Create GitHub issue"**.

 - Create a new child term for a Loinc code. If a current HPO term is close to what you need but you need a new child beneath it, you can select both the Loinc code and the candidate HPO term, right-click, select **"Suggest child term"**, fill in relevant information and submit.

 Note:
 1. If you do not have a GitHub account, you need to create one following their instructions ([GitHub website](https://github.com).

 2. The app currently does not support authentication with two-factor verifications[learn more](https://github.com/blog/1614-two-factor-authentication). If you implemented that feature, you may encounter issues during submission.

## Application

TO come later...
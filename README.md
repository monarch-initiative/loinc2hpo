[![Codacy Badge](https://api.codacy.com/project/badge/Grade/709c959bb0024403a667affaf2b9f476)](https://www.codacy.com/app/peter.robinson/loinc2hpo?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=monarch-initiative/loinc2hpo&amp;utm_campaign=Badge_Grade)
# org.monarchinitiative.loinc2hpo
Java library to map LOINC-encoded test results to Human Phenotype Ontology.

There are currently two modules, a core module and a graphical user interface (GUI) module. loinc2hpogui is a JavaFX app that intends to help with biocuration of LOINC code to HPO term mappings. To build and run the GUI, use the following command.
```
$ mvn clean package
$ java -jar loinc2hpogui/target/Loinc2HpoGui-jar-with-dependencies.jar 

```
To run the library code, enter the following
```
$ java -jar loinc2hpo-core/target/Loinc2HpoLib.jar download
```
This will download the hp.obo file (You need to manually download the LOINC Core Table file). There is a demo function that currently doesn't do anything
```
$ java -jar loinc2hpo-core/target/Loinc2HpoLib.jar demo
```
# documentation
Please refer to http://loinc2hpo.readthedocs.io/en/latest/.

# spring framework app
We are developing a separate app that will specialize in one functionality of this app - converting FHIR observations into HPO terms. The new app will be coded with the Spring framework and be able to interact with real-world hospital FHIR servers. Please refer to the app with the following link: https://github.com/OCTRI/fhir2hpo

# funding
We gratefully acknowledge funding by NCATS (CD2H project), 1U24TR002306
A NATIONAL CENTER FOR DIGITAL HEALTH INFORMATICS INNOVATION

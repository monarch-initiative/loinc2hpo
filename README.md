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

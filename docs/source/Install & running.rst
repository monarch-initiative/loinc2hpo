org.monarchinitiative.loinc2hpo
===============================
Java library to map LOINC-encoded test results to Human Phenotype Ontology

To build and run the GUI, use the following command.::

  $ mvn clean package
  $ java -jar loinc2hpogui/target/Loinc2HpoGui-jar-with-dependencies.jar


To run the library code, enter the following ::

  $ java -jar loinc2hpo-core/target/Loinc2HpoLib.jar download

This will download the hp.obo file (You need to manually download the LOINC Core Table file). There is a demo function that currently doesn't do anything ::

  $ java -jar loinc2hpo-core/target/Loinc2HpoLib.jar demo

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/709c959bb0024403a667affaf2b9f476)](https://www.codacy.com/app/peter.robinson/loinc2hpo?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=monarch-initiative/loinc2hpo&amp;utm_campaign=Badge_Grade)
[![Documentation Status](https://readthedocs.org/projects/loinc2hpo/badge/?version=latest)](https://loinc2hpo.readthedocs.io/en/latest/?badge=latest)
# LOINC2HPO annotation
If you want to access the LOINC to HPO mapping, refer to [loinc2hpoAnnotation repo](https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation).

If you want to contribute to LOINC to HPO mapping, read below.

# org.monarchinitiative.loinc2hpo
Java library to map LOINC-encoded test results to Human Phenotype Ontology.

*Important note on Java version* The library currently does not support Java 11. Please use Java 8.

There are currently two modules, a core module and a graphical user interface (GUI) module. loinc2hpogui is a JavaFX app that intends to help with biocuration of LOINC code to HPO term mappings. To build and run the GUI, use the following command.
```
$ mvn clean package
$ java -jar loinc2hpogui/target/Loinc2HpoGui-jar-with-dependencies.jar 

```

# documentation
Please refer to http://loinc2hpo.readthedocs.io/en/latest/.

# spring framework app
We are developing a separate app that will specialize in one functionality of this app - converting FHIR observations into HPO terms. The new app will be coded with the Spring framework and we strive to achieve enterprise-level quality. Please refer to the app with the following link: https://github.com/OCTRI/fhir2hpo

# funding
We gratefully acknowledge funding by NCATS (CD2H project), 1U24TR002306
A NATIONAL CENTER FOR DIGITAL HEALTH INFORMATICS INNOVATION

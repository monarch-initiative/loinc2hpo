Loinc2hpo Documentation
=======================

Loinc2hpo is a JavaFX app designed to convert FHIR observations into HPO terms. The app has three core functionalities:

- help mapping LOINC codes into candidate HPO terms
- help retrieve patient observations
- convert patient observations into HPO terms

Mapping LOINC to HPO terms requires significant efforts from biocurators. Loinc2hpo tries to simplify the process by automatically finding the best matching terms using Sparql query. With this app, collaborators can easily split LOINC codes of interest into smaller tasks and share their annotations.

.. toctree::
   :maxdepth: 2

   Intro to LOINC
   Intro to FHIR
   Our Mission
   Working logic
   Annotation logic
   Install & running
   Configuration
   Annotation Tutorial
   Consumption Tutorial
   Contact

GitHub repo
-----------
The source code of Loinc2hpo can be found at GitHub:
https://github.com/monarch-initiative/loinc2hpo

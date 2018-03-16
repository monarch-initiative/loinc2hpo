Loinc2hpo Tutorial
==================

.. toctree::
   :maxdepth: 2
   :caption: Contents:

Indices and tables
------------------


* :ref:`Intro to LOINC`
* :ref:`Intro to FHIR`
* :ref:`Our Mission`
* :ref:`Working logic`
* :ref:`Annotation logic`
* :ref:`Install & running`
* :ref:`Configuration`
* :ref:`Tutorial`
* :ref:`Contact`
* :ref:`search`

Loinc2hpo
---------
Loinc2hpo is a JavaFX app designed to convert FHIR observations into HPO terms. The app has three core functionalities:
- help mapping LOINC codes into candidate HPO terms
- help retrieve patient observations
- convert patient observations into HPO terms

Mapping LOINC to HPO terms requires a significant effort from biocurators. Loinc2hpo tries to simply the process by automatically finding the best matching terms using Sparql query. Collaborators can easily split LOINC codes of interest into smaller subsets and share their annotations.

For detailed instructions please refer to the documentation.

The source code of Loinc2hpo can be found at GitHub:
https://github.com/monarch-initiative/loinc2hpo

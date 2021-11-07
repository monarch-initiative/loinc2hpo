[![Codacy Badge](https://api.codacy.com/project/badge/Grade/709c959bb0024403a667affaf2b9f476)](https://www.codacy.com/app/peter.robinson/loinc2hpo?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=monarch-initiative/loinc2hpo&amp;utm_campaign=Badge_Grade)
[![Documentation Status](https://readthedocs.org/projects/loinc2hpo/badge/?version=latest)](https://loinc2hpo.readthedocs.io/en/latest/?badge=latest)


# loinc2hpo
A Java library to map tests results from [LOINC](https://loinc.org/) codes to  
[Human Phenotype Ontology.](https://hpo.jax.org/app/) terms.
For details, please see [Zhang et al. (2012)](https://pubmed.ncbi.nlm.nih.gov/31119199/) Semantic integration of clinical laboratory tests from electronic 
health records for deep phenotyping and biomarker discovery. *NPJ Digit Med*. 2019;2:32.


## LOINC2HPO annotation
The LOINC to HPO mapping file is available at the
[loinc2hpoAnnotation](https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation) repository.

## Using loinc2hpo
loinc2hpo is intended to be used as a software library. Selected functions are demonstrated in the CLI app.

## documentation
Please refer to http://loinc2hpo.readthedocs.io/en/latest/.

## spring framework app
We are developing a separate app that will specialize in one functionality of this app - converting FHIR observations into HPO terms. The new app will be coded with the Spring framework and we strive to achieve enterprise-level quality. Please refer to the app with the following link: https://github.com/OCTRI/fhir2hpo

## funding
We gratefully acknowledge funding by NCATS (CD2H project, A NATIONAL CENTER FOR DIGITAL HEALTH INFORMATICS INNOVATION), 1U24TR002306


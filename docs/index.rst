loinc2hpo Documentation
=======================

Loinc2hpo is a Java library designed to convert the findings of laboratory tests to HPO codes.
For instance, if the test is `LOINC 26515-7 Platelets [#/volume] in Blood <https://loinc.org/26515-7/>`_
and the outcome of the test is an abnormally low value, then we can infer the
`Human Phenotype Ontology (HPO) <https://hpo.jax.org/app/>`_ term
`Thrombocytopenia HP:0001873 <https://hpo.jax.org/app/browse/term/HP:0001873>`_.

The goal of this library is to encode EHR (Electronic Health Record) laboratory
data using HPO terms to extend the kinds of analysis that can be performed.
Laboratory results can be leveraged as phenotypic features for analysis.

  .. image:: images/mission.png
     :align: center
     :scale: 60 %

The library currently has three modules.

loinc2hpo-core
==============

This library contains the core functionality. It imports the annotation file from
the `loinc2hpoAnnotation <https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation>`_ repository
(loinc2hpo-annotations.tsv), and for any combination of LOINC Id (laboratory test) and
outcome, it finds the appropriate HPO term if one exists. We use set of internal codes
to represent lab outcomes.

+---------+------------------------------------------------------------------------------+
| Code    | Explanation                                                                  |
+=========+==============================================================================+
| L       | Low (below normal range). Used for quantitative tests (Qn).                  |
+---------+------------------------------------------------------------------------------+
| H       | High (above normal range). Used for quantitative tests (Qn).                 |
+---------+------------------------------------------------------------------------------+
| N       | Normal (within normal range). Used for quantitative tests (Qn).              |
+---------+------------------------------------------------------------------------------+
| NEG     | Negative (not present, a normal result). Used for ordinal tests (Ord)        |
+---------+------------------------------------------------------------------------------+
| POS     | Positive (present, an abnormal result). Used for ordinal tests (Ord)         |
+---------+------------------------------------------------------------------------------+
| NOM     | Nominal (an abnormal result). Used for nominal tests (Nom)                   |
+---------+------------------------------------------------------------------------------+

loinc2hpo-fhir
==============

This library provides an interface that extracts LOINC-encoded data from
`FHIR - Fast Healthcare Interoperability Resources <hl7.org/fhir>`_ data. Specifically,
it provides an interface that takes a FHIR `Observation <https://www.hl7.org/fhir/observation.html>`_
and attempts to extract a LOINC code and an outcome; if successful, these are passed to the
core loinc2hpo module to get the corresponding HPO term.

loinc2hpo-cli
=============

This is a command-line interface tool that can be used to obtain descriptive statistics or
perform quality control of the input files.

Contents
========

.. toctree::
   :maxdepth: 1

   intro_to_LOINC
   intro_to_FHIR
   FHIR_mapping
   getting_started

GitHub repo
-----------
The source code of Loinc2hpo can be found at GitHub:
https://github.com/monarch-initiative/loinc2hpo

Contact
-------

Peter Robinson
peter.robinson@jax.org

`The Jackson Laboratory <https://www.jax.org/>`_
10 Discovery Drive
Farmington, CT
USA

Xingmin Aaron Zhang
kingmanzhang@gmail.com

Curation
--------

We have developed a JavaFX application to curate loinc2hpo data. This app is not needed to
use the library, but may be of interest to potential contributors: https://github.com/pnrobinson/loinc2hpoMiner.

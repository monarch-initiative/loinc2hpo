============
FHIR Mapping
============

loinc2hpo can be used to convert LOINC-encoded laboratory results from FHIR.


Mapping LOINC to candidate HPO terms
====================================

LOINC observations have four main categories, quantitative(Qn), ordinal(Ord),
nominal (Nom) and narrative(Nar).
The majority of them are ``Qn`` (~50%) and ``Ord`` (~26%) and a smaller percentage
are ``Nom`` and ``Nar`` type (<10%). Currently, loinc2hpo maps QN, Ord, and Nom codes
but is not able to map Nar codes.

FHIR Interpretation codes
=========================

Test outcomes can represented with a code from the
`FHIR interpretation code valueset <https://www.hl7.org/fhir/valueset-observation-interpretation.html>`_.
HPO terms are qualitative. For instance,
`Thrombocytopenia HP:0001873 <https://hpo.jax.org/app/browse/term/HP:0001873>`_ does not
indicate whether there is a mild, moderate or severe degree of low blood platelets. Therefore
we map FHIR codes for different degrees of severity to the same internal code (these codes
are shown on the index page of this documentation).


Table 1: FHIR interpretation code set Mapping to internal code system

+------------------------------------+---------------------------+
|FHIR interpretation code value set  |internal FHIR subset       |
+-------+----------------------------+---------------------------+
|system |http://hl7.org/fhir/v2/0078 |FHIR                       |
+-------+----------------------------+--------+------------------+
|Code   | Meaning                    |Code    | Meaning          |
+=======+============================+========+==================+
|<      |Off scale low               |L       |low               |
+-------+----------------------------+--------+------------------+
|>      |Off scale high              |H       |high              |
+-------+----------------------------+--------+------------------+
|A      |Abnormal                    |A       |abnormal          |
+-------+----------------------------+--------+------------------+
|AA     |Critically abnormal         |A       |abnormal          |
+-------+----------------------------+--------+------------------+
|AC     |Anti-complementary          |POS     |present           |
|       |substances present          |        |                  |
+-------+----------------------------+--------+------------------+
|B      |Better                      |N       |normal            |
+-------+----------------------------+--------+------------------+
|D      |Significant change down     |L       |low               |
+-------+----------------------------+--------+------------------+
|DET    |Detected                    |POS     |present           |
+-------+----------------------------+--------+------------------+
|H      |High                        |H       |high              |
+-------+----------------------------+--------+------------------+
|HH     |Critically high             |H       |high              |
+-------+----------------------------+--------+------------------+
|HM     |Hold for Medical Review     |U       |unknown           |
+-------+----------------------------+--------+------------------+
|HU     |Very high                   |H       |high              |
+-------+----------------------------+--------+------------------+
|I      |Intermediate                |N       |normal            |
+-------+----------------------------+--------+------------------+
|IE     |Insufficient evidence       |U       |unknown           |
+-------+----------------------------+--------+------------------+
|IND    |Indeterminate               |U       |unknown           |
+-------+----------------------------+--------+------------------+
|L      |Low                         |L       |low               |
+-------+----------------------------+--------+------------------+
|LL     |Critically low              |L       |low               |
+-------+----------------------------+--------+------------------+
|LU     |Very low                    |L       |low               |
+-------+----------------------------+--------+------------------+
|MS     |Moderately susceptible.     |U       |unknown           |
|       |(microbiology)              |        |                  |
+-------+----------------------------+--------+------------------+
|N      |Normal                      |N       |normal            |
+-------+----------------------------+--------+------------------+
|ND     |Not Detected                |NEG     |not present       |
+-------+----------------------------+--------+------------------+
|NEG    |Negative                    |NEG     |not present       |
+-------+----------------------------+--------+------------------+
|NR     |Non-reactive                |NEG     |not present       |
+-------+----------------------------+--------+------------------+
|NS     |Non-susceptible             |U       |unknown           |
+-------+----------------------------+--------+------------------+
|null   |No range defined or normal  |U       |unknown           |
|       |ranges don't apply          |        |                  |
+-------+----------------------------+--------+------------------+
|OBX    |Interpretation qualifiers   |U       |unknown           |
|       |in separate OBX segments    |        |                  |
+-------+----------------------------+--------+------------------+
|POS    |Positive                    |POS     |positive          |
+-------+----------------------------+--------+------------------+
|QCF    |Quality Control Failure     |U       |unknown           |
+-------+----------------------------+--------+------------------+
|R      |Resistant                   |U       |unknown           |
+-------+----------------------------+--------+------------------+
|RR     |Reactive                    |POS     |present           |
+-------+----------------------------+--------+------------------+
|S      |Susceptible                 |U       |unknown           |
+-------+----------------------------+--------+------------------+
|SDD    |Susceptible-dose dependent  |U       |unknown           |
+-------+----------------------------+--------+------------------+
|SYN-R  |Synergy - resistant	     |U       |unknown           |
+-------+----------------------------+--------+------------------+
|SYN-S  |Synergy - susceptible	     |U       |unknown           |
+-------+----------------------------+--------+------------------+
|TOX    |Cytotoxic substance present |POS     |present           |
+-------+----------------------------+--------+------------------+
|U      |Significant change up       |H       |high              |
+-------+----------------------------+--------+------------------+
|VS     |Very susceptible.           |U       |unknown           |
|       |(microbiology)              |        |                  |
+-------+----------------------------+--------+------------------+
|W      |Worse                       |A       |abnormal          |
+-------+----------------------------+--------+------------------+
|WR     |Weakly reactive             |POS     |present           |
+-------+----------------------------+--------+------------------+


The following graph summarizes the mapping.

  .. image:: images/annotation_scheme.png
    :width: 400
    :alt: Annotation scheme

Nominal outcomes
================

Nominal observations can use coding systems that are more difficult to handle.
For example, `Loinc 600-7 <https://loinc.org/600-7/>`_ (Bacteria identified in Blood by Culture)
may use a SNOMED concept to represent the
finding that *Staphylococcus aureus* is detected::

  "coding":[{
    "system": "http://snomed.info/sct",
    "code": "3092008",
    "display": "Staphylococcus aureus"
  }]

We currently map this to  `Bacteremia HP:0031864 <https://hpo.jax.org/app/browse/term/HP:0031864>`_,
but this term does not contain information about which bacterium was idenfitied in the blood.

We are currently extending the annotations to enable one to indicate what kind of bacteremia. In the above mentioned case,
one would use the NCBI `Taxonomy ID: 1280 <https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?lvl=0&id=1280>`_
for *Staphylococcus aureus*.







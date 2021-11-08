Introduction to LOINC
=====================

`LOINC <https://loinc.org/>`_ (Logical Observation Identifiers Names and Codes)
provides a set of universal names and ID codes for identifying laboratory and clinical
test results.

LOINC currently provides ~92,000 entries that define the names and IDs of laboratory tests.
The following shows three examples of LOINC codes:

  .. image:: images/loinc_examples.png

Each LOINC entry represents a laboratory test.

Parts of LOINC entry
--------------------

- ``LOINC``: unique identifier
- ``Name``: structured term name
- ``Component``: defines the analyte in the test
- ``Property``: defines "kinds of quantities". It can be divided into five several categories, mass, substance, catalytic activity, and number or counts. Each category is further divided into subclasses, for example MCnc or "mass concentration" is a subclass of "mass" property, while ``NCnc`` or "number of concentration (count/vol)" and ``Naric`` or "number aeric (number per area)" are subclasses of counts.
- ``Time``: defines whether a measurement was made at a moment, or aggregated from a series of physiologic states. The three examples are all ``PT`` ("points"), meaning that they are measurements at a single time point. As an example, a test on "daily urine amount" will be labeled as ``24H`` ("24 hours").
- ``Aspect``: defines a modifier for a measurement over a duration. For example, "8H^max heart rate" means the "max" heart rate measured during an 8-hour period. Min, max, first, last, mean typically appear here.
- ``System``: can be considered as the specimen for the test, such as "serum", "blood", "urine", "cerebrospinal fluid" etc.
- ``Scale``" defines the scale of the measurement. Scale is the most important information for our application. The following table summarizes possible values of ``scale``.
- ``Method``: defines the method used for the measurement.


Table 1: LOINC Scale Types

+----------------+------+-------------------------------------------------------------------------------------+
| Scale Type     | Abbr.| Description                                                                         |
+================+======+=====================================================================================+
| Quantitative   | Qn   | The result of the test is a numeric value that relates to a continuous numeric      |
|                |      | scale.                                                                              |
+----------------+------+-------------------------------------------------------------------------------------+
| Ordinal        | Ord  | Ordered categorical responses, e.g., positive, negative;                            |
+----------------+------+-------------------------------------------------------------------------------------+
| Quantitative   | OrdQn| Test can be reported as either Ord or Qn.                                           |
+----------------+------+-------------------------------------------------------------------------------------+
| Nominal        | Nom  | Nominal or categorical responses that do not have a natural ordering.               |
+----------------+------+-------------------------------------------------------------------------------------+
| Narrative      | Nar  | Text narrative.                                                                     |
+----------------+------+-------------------------------------------------------------------------------------+
| “Multi”        | Multi| Many separate results structured as one text “glob”                                 |
+----------------+------+-------------------------------------------------------------------------------------+
| Document       | Doc  | A document that could be in many formats (XML, narrative, etc.)                     |
+----------------+------+-------------------------------------------------------------------------------------+
| Set            | Set  | Used for clinical attachments                                                       |
+----------------+------+-------------------------------------------------------------------------------------+



``Qn``, ``Ord`` and ``Nom`` are the three most frequently used LOINC codes,
accounting for about 99% of data. ``Qn`` typically makes up about 80% of cases.















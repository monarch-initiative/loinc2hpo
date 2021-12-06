Getting started
===============

Overview
--------

loinc2hpo is a Java library. It requires at least Java 11. It is intended to be used by other applications
for specific purposes. This tutorial shows how to install loinc2hpo and how to use it in a typical Java program.

Installation
------------
First clone the library from GitHub. ::

    git clone https://github.com/monarch-initiative/loinc2hpo.git

Now use maven to install the library. ::

    cd loinc2hpo
    mvn install

We plan to place loinc2hpo on maven central in the future, which will make this step unnecessary.

Using loinc2hpo in maven projects
---------------------------------

To use the loinc2hpo in your own Java project, add the following to your pom file.

.. code-block:: XML

  <properties>
    <loinc2hpo.version>1.7.0</loinc2hpo.version>
  </properties>
  (...)
  <dependencies>
    <dependency>
      <groupId>org.monarchinitiative</groupId>
      <artifactId>loinc2hpo-core</artifactId>
      <version>${loinc2hpo.version}</version>
    </dependency>
    <dependency>
      <groupId>org.monarchinitiative</groupId>
      <artifactId>loinc2hpo-fhir</artifactId>
      <version>${loinc2hpo.version}</version>
    </dependency>
  </dependencies>

The ``loinc2hpo-fhir`` module is only required for working with FHIR of course.


Core module
~~~~~~~~~~~

The loinc2hpo annotation file referenced in the following code is available from the
`loinc2hpoAnnotation repository <https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation>`_.
To run the code, ingest the annotation file and then pass



.. code-block:: java

  import org.monarchinitiative.loinc2hpocore.codesystems.ShortCode;
  import org.monarchinitiative.phenol.ontology.data.TermId;
  import org.monarchinitiative.loinc2hpocore.Loinc2Hpo;
  import org.monarchinitiative.loinc2hpocore.loinc.LoincId;
  import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;

  String annot_file = "loinc2hpo-annotations.tsv";
  Loinc2Hpo loinc2Hpo = new Loinc2Hpo(annot_file);
  LoincId loincId = new LoincId("26515-7");
  Optional<Hpo2Outcome> opt = loinc2Hpo.query(loincId, Outcome.LOW());
  if (opt.isPresent()) {
    Hpo2Outcome hpo2outcome = opt.get();
    TermId hpoId = hpo2outcome.getHpoId();
    Outcome outcome = hpo2outcome.outcome();
    // do something with the HPO term and the outcome (low in this example).
  }


FHIR module
~~~~~~~~~~~

The FHIR module is intended to be used with `HAPI FHIR <https://hapifhir.io/>`_.
It can be used with the FHIR specifications DSTU3, R4, or R5.

.. code-block:: java

    import org.monarchinitiative.loinc2hpofhir.Loinc2HpoFhir;
    import org.hl7.fhir.r5.model.*;
    import org.monarchinitiative.loinc2hpocore.codesystems.Outcome;
    import org.monarchinitiative.loinc2hpocore.loinc.LoincId;

    String annot_file = "loinc2hpo-annotations.tsv";
    Loinc2HpoFhir loinc2hpoFHIR = new Loinc2HpoFhir(String path);
    // The following is a R5 Observation
    Observation observation = getObservationFromSomewhere(); // your code does this
    Optional<Hpo2Outcome> opt = loinc2hpoFHIR.r5(observation);
    if (opt.isPresent()) {
      Hpo2Outcome hpo2outcome = opt.get();
      TermId hpoId = hpo2outcome.getHpoId();
      Outcome outcome = hpo2outcome.outcome();
      // do something with the HPO term and the outcome.
    }


The ``Loinc2HpoFhir`` has analogous methods called ``dstu3`` and ``r4`` for the other
FHIR versions.
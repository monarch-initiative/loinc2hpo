Consumption Tutorial
====================

Overview
--------
In this tutorial, we are going to demonstrate how to use the core library (loinc2hpo-core) to transform LOINC-coded lab tests into HPO terms. The lab tests can be encoded in any standards, such as i2b2, OMOP, PCORNET etc. As long as they are identified with LOINC codes and that you can interpret the results (see below), we can transform them into HPO-coded phenotypes. This tutorial expects you know Java and maven.

Installation
------------

Because the library is not yet on maven central, you have to install the library before you can use it in your project.

Cd into the *loinc2hpo/* directory, then run

> mvn install

Then you can put the JAR file into your project classpath. If you are using maven to manage dependencies, put the following section into your pom file.

:: code-block xml

  <dependency>
      <groupId>org.monarchinitiative</groupId>
      <artifactId>loinc2hpo-core</artifactId>
      <version>1.1.7-SNAPSHOT</version>
  </dependency>


Quick Start
~~~~~~~~~~~

Download the annotation file from the `loinc2hpoAnnotation Github repository <https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation/tree/master/Data/TSVSingleFile>`_.

Instantiate a Loinc2Hpo object

:: java
  Loinc2Hpo loinc2Hpo = new Loinc2Hpo(path_to_annotation_file);

For a given lab test, assume that you can extract the LOINC id and are able to interpret the result as L (lower than normal), N (normal), H (higher than normal), NEG (negative) or POS (positive), then call the following method:

:: code-block java
  public HpoTerm4TestOutcome query(LoincId loincId, String system, String id) throws LoincCodeNotAnnotatedException, AnnotationNotFoundException {
        ...
  }

For example, suppose the test is LOINC 2823-3 Potassium in Serum or plasma, the result is L, then your code is:

:: code-block java
  HpoTerm4TestOutcome phenotype = loinc2hpo.query(new LoincId("2823-3"), "FHIR", "L");
  //is phenotype negated: false in this case (it would be true if the result is normal)
  System.out.println(phenotype.isNegated);
  //HPO term id: HP:0002900
  System.out.println(phenotype.getId().getValue();


Step by step tutorial
~~~~~~~~~~~~~~~~~~~~~
We are going to break down the transformation process into two steps.

In step one, interpret lab test result. Most lab tests that you see are reported as numeric values. In order to use this library to map the result into an HPO term, it has to be compared with a reference range in order to assign an interpretation code, *L* for *lower than normal*, *N* for *normal* and *H* for *higher than normal*. It is a trivial task if you data comes with applicable reference range, but it can also be quite tricky. For one thing, it is possible that your dataset does not have reference ranges. For the other, it is possible that the reference ranges are too general and do not apply to every lab record, for example, you know the reference ranges in the general population but a lab test is on a infant. We want to point out that the library does not provide a solution to solve either issues, but we can give some suggestions based on our and other people's experience. For example, the most straightfoward way is to ask the data provider (namely, clinical labs) to provide you their references if they are lacking in your dataset, and ideally to get the applicable reference range for each lab record. If that is not feasible, we saw people treating the entire population as "normal", and define arbitrary thresholds as the reference ranges (for example, 2.5% and 97.5% percentiles as lower and upper normal range). We also noticed that in some datasets, a lab test is flagged if the result is abnormal. In this case, one can deduce the normal range from the unflagged subset.

Once you can properly interpret the lab tests, step two is to call the *loinc2hpo* app to return the mapped HPO term. You need to download the annotation file from our `loinc2hpoAnnotation Github repository <https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation/tree/master/Data/TSVSingleFile>`_. Instantiate the Loinc2Hpo class with the path to the annotation file:

:: code-block java
  Loinc2Hpo loinc2Hpo = new Loinc2Hpo(path_to_annotation_file);

For a individual lab test, extract the LOINC id (let's assign it to variable *loincId*), suppose you have successfully interpreted the result with a code, say "L", and then the remaining task is to call the query method:

:: code-block java
  HpoTerm4TestOutcome phenotype = loinc2hpo.query(new LoincId(loincId), "FHIR", "L");

You will get a *HpoTerm4TestOutcome* instance as the result, which basically contains two pieces of information, an HPO term, and a boolean value indicating whether the term should be negated. The negation value is false if the result is abnormal, and true if the result is normal. For example, the resulting HPO is Hypokalemia HP:0002900 for blood potassium measurement that is too low, and NOT Abnormal blood potassium concentration HP:0011042 if the measurement is within reference range. We have to use the negation because HPO does not encode normal phenotypes.

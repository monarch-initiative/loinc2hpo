Consumption Tutorial
====================

Overview
--------
In this tutorial, we are going to demonstrate how to use the core library (loinc2hpo-core) to transform LOINC-coded lab tests into HPO terms. Although our original paper focused on tranforming FHIR-encoded lab tests into HPO term, the loinc2hpo tool can be used for lab tests encoded in any standards, such as i2b2, OMOP, PCORNET, **as long as** they are identified with LOINC codes and that you can interpret the results (see below). This tutorial expects you know Java and maven.

Installation
------------

Because the library is not yet on maven central, you have to install the library before you can use it in your project.

Cd into the *loinc2hpo/* directory, then run

> mvn install

Then you can put the JAR file into your project classpath. If you are using maven to manage dependencies, put the following section into your pom file.

.. code-block:: XML

  <dependency>
      <groupId>org.monarchinitiative</groupId>
      <artifactId>loinc2hpo-core</artifactId>
      <version>1.1.7-SNAPSHOT</version>
  </dependency>


Quick Start
~~~~~~~~~~~

Download the annotation file from the `loinc2hpoAnnotation Github repository <https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation/tree/master/Data/TSVSingleFile>`_.

Instantiate a Loinc2Hpo object

.. code-block:: java

  Loinc2Hpo loinc2Hpo = new Loinc2Hpo(path_to_annotation_file);

For a given lab test, assume that you can extract the LOINC id and are able to interpret the result as L (lower than normal), N (normal), H (higher than normal), NEG (negative) or POS (positive), then call the following method:

.. code-block:: java

  public HpoTerm4TestOutcome query(LoincId loincId, String system, String id) throws LoincCodeNotAnnotatedException, AnnotationNotFoundException {
        ...
  }

For example, suppose the test is LOINC 2823-3 Potassium in Serum or plasma, the result is L, then your code is:

.. code-block:: java

  HpoTerm4TestOutcome phenotype = loinc2hpo.query(new LoincId("2823-3"), "FHIR", "L");
  //is phenotype negated: false in this case (it would be true if the result is normal)
  System.out.println(phenotype.isNegated);
  //HPO term id: HP:0002900
  System.out.println(phenotype.getId().getValue();


Step-by-step tutorial
~~~~~~~~~~~~~~~~~~~~~
We are going to break down the transformation process into two steps.

I. In step one, interpret lab test result.

Most lab tests that you see are reported as numeric values. In order to use this library to map the result into an HPO term, it has to be compared with a reference range in order to assign an interpretation code, *L* for *lower than normal*, *N* for *normal* and *H* for *higher than normal*. It is a trivial task if you data comes with applicable reference range, but it can also be quite tricky. For one thing, it is possible that your dataset does not have reference ranges. For the other, it is possible that the reference ranges are too general and do not apply to every lab record, for example, you know the reference ranges in the general population but a lab test is on a infant. We want to point out that the library does not provide a solution to solve either issues, but we can give some suggestions based on our and other people's experience. For example, the most straightfoward way is to ask the data provider (namely, clinical labs) to provide you their references if they are lacking in your dataset, and ideally to get the applicable reference range for each lab record. If that is not feasible, we saw people treating the entire population as "normal", and define arbitrary thresholds as the reference ranges (for example, 2.5% and 97.5% percentiles as lower and upper normal range). We also noticed that in some datasets, a lab test is flagged if the result is abnormal. In this case, one can deduce the normal range from the unflagged subset.


For an individual lab test, extract the LOINC id (let's assign it to variable *loincId*). Assume you have determined an applicable reference range [lowest_normal_value, highest_normal_value], we can assign an interpretation code to the result:

.. code-block:: java

  String code_system = "FHIR";
  String code_id = null;
  if (result < lowest_normal_value) {
    code_id = "L";
  } else if (result > highest_normal_value) {
    code_id = "H";
  } else {
    code_id = "N";
  }

Note that the variable *code_system* specifies the namespace of the interpretation code. Internally, we used a subset of FHIR interpretation codes for our annotation file. Therefore, this value is always "FHIR" (see Exception).

If the LOINC term is expected to report a binary value, absence or presence, the interpretation code should be "NEG" or "POS", respectively. We noticed real world data is quite messy in reporting this type of lab tests. Some datasets uses free texts in the result field with phrases like "positive", "POSITIVE", "negative", "abnormal". If this is the case, using syntactic matching might suffice. In other datasets, the original numerical values are reported. In this case, one still have to determine the applicable reference range and manually transform them into binary results.

.. code-block:: java

  //syntactic matching
  if (result.toLower().contains("pos") {
    code_id = "POS";
  } else if (result.toLower().contains("neg"){
    code_id = "NEG";
  } else {
    //handle this case properly
    System.out.println("unknown result");
  }

  //comparing with reference ranges
  if (result > threshold) {
    code_id = "POS";
  } else {
    code_id = "NEG";
  }

**Exception**
A small subset of our annotations used SNOMED-CT concepts, in which case the namespace is "snomed-ct" and the code is using SNOMED-CT concept id. These lab tests are called nominal in the LOINC term. For example, if you want to convert the result of lab test LOINC 5778-6 Color of Urine into HPO term, you need to query with the SNOMED-CT concepts:

.. code-block:: java

  code_system = "snomed-ct"
  code_id = "449071000124107";


II. After you have successfully interpreted the result with a code, the remaining task is to call the query method.

You need to download the annotation file from our `loinc2hpoAnnotation Github repository <https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation/tree/master/Data/TSVSingleFile>`_. Instantiate the Loinc2Hpo class with the path to the annotation file:

.. code-block:: java

  Loinc2Hpo loinc2Hpo = new Loinc2Hpo(path_to_annotation_file);

Then call the query method to return the phenotype term.

.. code-block:: java

  HpoTerm4TestOutcome phenotype = loinc2hpo.query(new LoincId(loincId), code_system, code_id);
  //print out the result
  System.out.println(phenotype.isNegated());
  System.out.println(phenotype.getId().getValue());

You will get a *HpoTerm4TestOutcome* instance as the result, which basically contains two pieces of information, an HPO term, and a boolean value indicating whether the term should be negated. The negation value is false if the result is abnormal, and true if the result is normal. For example, the resulting HPO is Hypokalemia HP:0002900 for blood potassium measurement that is too low, and NOT Abnormal blood potassium concentration HP:0011042 if the measurement is within reference range. We have to use the negation because HPO does not encode normal phenotypes.

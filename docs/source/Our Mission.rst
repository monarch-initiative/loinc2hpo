Our Mission
===========

Our goal with this app is to extract patient phenotypes in HPO terms from Electronic Health Records that complies with the FHIR standards: As illustrated below, each patient may have dozens or hundreds of associated medical observations. We hope our app will be able to analyze them and output a "phenopacket", a list of HPO terms, that describes the patient.

  .. image:: images/mission.png
     :align: center
     :scale: 60 %


To achieve our goal, we need to do the following:

- *Map LOINC codes to candidate HPO terms*. This steps needs domain experts and will be done manually by our biocurators.

- *Retrieve patient observations*. We will use RESTful API technology to achieve this. The hapi-fhir library provides such functionalities so this can be done trivially. We will also collaborate with other institutions and hospitals to gain access to large patient cohorts.

- *Convert patient observations into phenopackets*. Our app will analyze the relevant fields from each observation and then output the corresponding HPO term that describes the patient. By analyzing all observations related to one patient, we will be able to create a phenopacket for this patient. We are still refining our app for this process.


Having a phenopacket for an individual is essential for disease prediction and patient clustering. We hope to extend our app so that it can be used by physicians to assist patient diagnosis; we also hope that the phenopackets can help academic researchers in study disease mechanisms.
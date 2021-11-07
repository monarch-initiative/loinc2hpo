Introduction to FHIR
====================

Fast Healthcare Interoperability Resources (FHIR) is a set of standards created by HL7 that aims to improve the exchange and interoperability of healthcare information. Consider the following case:

  .. image::images/fhir_intro.png

The patient visits clinics, hospitals and receive medical tests in clinical labs, or visits drug stores. It would be nice if the physicians can retrieve his test results from other providers, or the drug store can automatically receive the prescriptions for the patient. The above is not possible without a standardization of information exchange. This is how FHIR come into play.

FHIR is also extremely helpful in reusing Electronic Health Record for scientific research. EHRs provide a wealth of medical information, but their lack of standardization presents a huge hurdle for their reusability. With FHIR, one can easily process patient record stored in different EHR systems.


Resources
---------

FHIR uses RESTful API technology for data exchange. It takes all medical information as individual pieces of resources. For example, ``Administration``-related resources are mainly about identifications (e.g. about patient, about practioner, about device ...). Diagnostics resources are those about observations, specimen etc. There are also resources about medications and health insurances. Resources are typically stored and exchanged in JSon format. There are unique identifiers in each resource that can cross reference to other another so that it is possible to find, say, all resources related to one patient.

  .. image:: images/FHIR_resource1.png
  .. image:: images/FHIR_resource2.png
  .. image:: images/FHIR_resource3.png

``Observation``

Among all the resources, ``Observation`` is the most central one in EHR. ``Observation`` describes a medical test result for one patient. The following JSon snippet is an exerpt from a real world (ref: `hl7.org <https://www.hl7.org/fhir/observation-example-f001-glucose.json.html>`_).

Information about the ID of the resource. ::

  {
  "resourceType": "Observation",
  "id": "f001",
  "text": {
    "status": "generated",
    "div": "<div xmlns=\"http:\/\/www.w3.org\/1999\/xhtml\"><p><b>Generated Narrative with Details<\/b><\/p><p><b>id<\/b>: f001<\/p><p><b>identifier<\/b>: 6323 (OFFICIAL)<\/p><p><b>status<\/b>: final<\/p><p><b>code<\/b>: Glucose [Moles\/volume] in Blood <span>(Details : {LOINC code '15074-8' = 'Glucose [Moles\/volume] in Blood', given as 'Glucose [Moles\/volume] in Blood'})<\/span><\/p><p><b>subject<\/b>: <a>P. van de Heuvel<\/a><\/p><p><b>effective<\/b>: 02\/04\/2013 9:30:10 AM --&gt; (ongoing)<\/p><p><b>issued<\/b>: 03\/04\/2013 3:30:10 PM<\/p><p><b>performer<\/b>: <a>A. Langeveld<\/a><\/p><p><b>value<\/b>: 6.3 mmol\/l<span> (Details: UCUM code mmol\/L = 'mmol\/L')<\/span><\/p><p><b>interpretation<\/b>: High <span>(Details : {http:\/\/hl7.org\/fhir\/v2\/0078 code 'H' = 'High', given as 'High'})<\/span><\/p><h3>ReferenceRanges<\/h3><table><tr><td>-<\/td><td><b>Low<\/b><\/td><td><b>High<\/b><\/td><\/tr><tr><td>*<\/td><td>3.1 mmol\/l<span> (Details: UCUM code mmol\/L = 'mmol\/L')<\/span><\/td><td>6.2 mmol\/l<span> (Details: UCUM code mmol\/L = 'mmol\/L')<\/span><\/td><\/tr><\/table><\/div>"
  },
  "identifier": [
    {
      "use": "official",
      "system": "http:\/\/www.bmc.nl\/zorgportal\/identifiers\/observations",
      "value": "6323"
    }
  ],
  "status": "final",

Information about the test, identified with a LOINC code.  ::

  "code": {
    "coding": [
      {
        "system": "http:\/\/loinc.org",
        "code": "15074-8",
        "display": "Glucose [Moles\/volume] in Blood"
      }
    ]
  },

Information about the subject/patient of this resource. ::

  "subject": {
    "reference": "Patient\/f001",
    "display": "P. van de Heuvel"
  },

Some meta data about this test: ::

  "effectivePeriod": {
    "start": "2013-04-02T09:30:10+01:00"
  },
  "issued": "2013-04-03T15:30:10+01:00",
  "performer": [
    {
      "reference": "Practitioner\/f005",
      "display": "A. Langeveld"
    }
  ],

Measured value and reference range: ::

  "valueQuantity": {
    "value": 6.3,
    "unit": "mmol\/l",
    "system": "http:\/\/unitsofmeasure.org",
    "code": "mmol\/L"
  },

  "referenceRange": [
    {
      "low": {
        "value": 3.1,
        "unit": "mmol\/l",
        "system": "http:\/\/unitsofmeasure.org",
        "code": "mmol\/L"
      },
      "high": {
        "value": 6.2,
        "unit": "mmol\/l",
        "system": "http:\/\/unitsofmeasure.org",
        "code": "mmol\/L"
      }
    }
  ]

Interpretation from physicians: ::

  "interpretation": {
    "coding": [
      {
        "system": "http:\/\/hl7.org\/fhir\/v2\/0078",
        "code": "H",
        "display": "High"
      }
    ]
  },
  }

``Patient``

``Patient`` manages all relevant information about the patient, such as name, address, sex, etc. The following snippet is an example in JSon (ref: `hl7.org <https://www.hl7.org/fhir/patient-example-f001-pieter.json>`_). The code is pretty self-explanatory.

::

  {
  "resourceType": "Patient",
  "id": "f001",
  "text": {
    "status": "generated",
    "div": "<div xmlns=\"http:\/\/www.w3.org\/1999\/xhtml\"><p><b>Generated Narrative with Details<\/b><\/p><p><b>id<\/b>: f001<\/p><p><b>identifier<\/b>: 738472983 (USUAL), ?? (USUAL)<\/p><p><b>active<\/b>: true<\/p><p><b>name<\/b>: Pieter van de Heuvel <\/p><p><b>telecom<\/b>: ph: 0648352638(MOBILE), p.heuvel@gmail.com(HOME)<\/p><p><b>gender<\/b>: male<\/p><p><b>birthDate<\/b>: 17\/11\/1944<\/p><p><b>deceased<\/b>: false<\/p><p><b>address<\/b>: Van Egmondkade 23 Amsterdam 1024 RJ NLD (HOME)<\/p><p><b>maritalStatus<\/b>: Getrouwd <span>(Details : {http:\/\/hl7.org\/fhir\/v3\/MaritalStatus code 'M' = 'Married', given as 'Married'})<\/span><\/p><p><b>multipleBirth<\/b>: true<\/p><h3>Contacts<\/h3><table><tr><td>-<\/td><td><b>Relationship<\/b><\/td><td><b>Name<\/b><\/td><td><b>Telecom<\/b><\/td><\/tr><tr><td>*<\/td><td>Emergency Contact <span>(Details : {http:\/\/hl7.org\/fhir\/v2\/0131 code 'C' = 'Emergency Contact)<\/span><\/td><td>Sarah Abels <\/td><td>ph: 0690383372(MOBILE)<\/td><\/tr><\/table><h3>Communications<\/h3><table><tr><td>-<\/td><td><b>Language<\/b><\/td><td><b>Preferred<\/b><\/td><\/tr><tr><td>*<\/td><td>Nederlands <span>(Details : {urn:ietf:bcp:47 code 'nl' = 'Dutch', given as 'Dutch'})<\/span><\/td><td>true<\/td><\/tr><\/table><p><b>managingOrganization<\/b>: <a>Burgers University Medical Centre<\/a><\/p><\/div>"
  },
  "identifier": [
    {
      "use": "usual",
      "system": "urn:oid:2.16.840.1.113883.2.4.6.3",
      "value": "738472983"
    },
    {
      "use": "usual",
      "system": "urn:oid:2.16.840.1.113883.2.4.6.3"
    }
  ],
  "active": true,
  "name": [
    {
      "use": "usual",
      "family": "van de Heuvel",
      "given": [
        "Pieter"
      ],
      "suffix": [
        "MSc"
      ]
    }
  ],
  "telecom": [
    {
      "system": "phone",
      "value": "0648352638",
      "use": "mobile"
    },
    {
      "system": "email",
      "value": "p.heuvel@gmail.com",
      "use": "home"
    }
  ],
  "gender": "male",
  "birthDate": "1944-11-17",
  "deceasedBoolean": false,
  "address": [
    {
      "use": "home",
      "line": [
        "Van Egmondkade 23"
      ],
      "city": "Amsterdam",
      "postalCode": "1024 RJ",
      "country": "NLD"
    }
  ],
  "maritalStatus": {
    "coding": [
      {
        "system": "http:\/\/hl7.org\/fhir\/v3\/MaritalStatus",
        "code": "M",
        "display": "Married"
      }
    ],
    "text": "Getrouwd"
  },
  "multipleBirthBoolean": true,
  "contact": [
    {
      "relationship": [
        {
          "coding": [
            {
              "system": "http:\/\/hl7.org\/fhir\/v2\/0131",
              "code": "C"
            }
          ]
        }
      ],
      "name": {
        "use": "usual",
        "family": "Abels",
        "given": [
          "Sarah"
        ]
      },
      "telecom": [
        {
          "system": "phone",
          "value": "0690383372",
          "use": "mobile"
        }
      ]
    }
  ],
  "communication": [
    {
      "language": {
        "coding": [
          {
            "system": "urn:ietf:bcp:47",
            "code": "nl",
            "display": "Dutch"
          }
        ],
        "text": "Nederlands"
      },
      "preferred": true
    }
  ],
  "managingOrganization": {
    "reference": "Organization\/f001",
    "display": "Burgers University Medical Centre"
  }
  }

``Patient`` is essential in cases when the interpretation of an observation, e.g. height or weight, dependents on the sex, age or other relevant information of the patient. In this case, one can retrieve ``Patient`` with the link in the "subject" field of ``Observation``.

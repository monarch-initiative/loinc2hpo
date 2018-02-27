# loinc2hpo Changelog

## current
* v 1.0.1

### loinc2hpo-core
* Adding enums for FHIR observation codes

## v0.0.2

### loinc2hpogui

* Added FHIR to HPO conversion dialog

### loinc2hpo-core

* added FHIR parsing

## v1.0.1
This is the first official release. Many features are added.

### loinc2hpogui
Multiple features are added. This will be the baseline for future tracking.

### loinc2hpo-core

* The core change is completely switching to FHIR to parse `observation` and `patient` resources. 

* Redesigned the annotation class. 
  - Use Code (system/namespace, code) to ensure uniqueness
  - An HPO term for a coded result is wrapped in a class that also indicate whether the term should be negated.
  - A complete annotation for a Loinc code contains many `Code` - `(HPO term, isNegated)` list. 
  
* Redesigned the logic process from observation to HPO term. 
  - The app first looks at whether the observation has an interpretation field. 
    - If it does, it will first try to find an annotation for the interpretation code directly; 
    - if it fails, it will try to convert convert the interpretation code to the internal code and then find the corresponding HPO term. 
  
  - If the app fails the last step, it will try to use the raw value and interpret it with the reference ranges.  




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
  
## v1.0.2

* Build Jar with all dependencies with maven-assembly-plugin

* Add META-INF/services to Core module because it appears that is what Jar requires

## v1.0.3

* Refactor pom files. 

* Refactor gitignore file. 

## v1.0.4

* Allow adding multiple labels to Github issues

* Disable the function to clear annotation fields during manual query

* Loinc entries change color if they have been annotated

* Add tooltips to HPO listview and treeview

* Allow user to switch to previously selected Loinc list

* Allow user to categorize Loinc entries

## v1.1.0 

* New develop version

Additional changes for this version

* Change menu `Edit` to `Configuration`
- [ ] update tutorial

* Create new features that allow user to manipulate a session
  
* Automatically retrieve information from auto-saved data for last session

## v1.1.1

* Session data now only saves terms for low, intermediate, and high value, instead for all 6 internal codes

* Basic and Advanced data are stored separately

## v1.1.2

* Session data now saves to a universal TSV file

* Restrict internal mappings

  Qn will not be mapped to "Presence" or "Absence" and Ord (of "Presence" type) will not be mapped to "high", "low", "normal"
  
* Internal codes changed match FHIR

  "system" is renamed to "FHIR";
  Code for "presence" changed from "P" to "POS", code for "not presence" changed from "NP" to "NEG" to be consistent with FHIR

* Show version in "About" message



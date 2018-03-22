Annotation Logic
================

The key to successful annotation is to anticipate what kinds of outcome are for a LOINC code. Since the outcome is strongly associated with the ``Scale`` parameter of LOINC, we will describe three main types separately. For more detailed description of LOINC, refer to ``Intro to LOINC``.


``Qn``
------

``Qn`` or "quantitative" is the largest category of all LOINC codes, accounting for >80% in real world applications. Observations of this type have a numeric value as its outcome, such as the number of blood cells or the concentration of a substance. Annotating ``Qn`` is relatively straightforward: pick three HPO terms corresponding to the outcome of the observation when the measured value is low, intermediate or high. Here is an example for LOINC ``2823-3`` ("Potassium in Serum or Plasma"):

    .. image:: images/annotation_example1.png

**Term Negation**
As you can see from the above example, we assigned the negated form of "HP:0011015 Abnormality of blood glucose concentration" to the intermediate value. Intermediate value usually means "normal", e.g. in this case, so by default the term that you choose for intermediate value is always negated. However, there are cases when this is not true, e.g. LOINC 9269-2 Glasgow coma score total, where the intermediate value is not interpreted as "no coma" but instead "mild coma". So you will need to uncheck the "negate" checkbox in this case.

``Ord``
-------

``Ord`` or "ordinal" is the second largest category of LOINC codes, accounting for ~14%. Ordinal type of observation has a ordered set of values as its outcome. For example, "Presence, Absence", "1, 2, 3", but the values have no linear relationship to one another. Since ordinal results are comparable to each other, many observations also use the an interpretation code to indicate whether the result is too low, normal or too high. "Presence" or "positive" can be considered as a value that is "too high", while "absence" or "negative" usually indicates that the measured value is not "too high". Therefore, one simple way to annotate this type is to pick two HPO terms, one for "too high" and the other for "intermediate":

    .. image:: images/annotation_example2.png

In some occasions it might become necessary to annotate each value of the outcome. The procedure becomes very similar to how we annotate nominal type LOINC (see below).

``Nom``
-------
``Nom`` or "nominal" accounts for a small percentage of LOINC codes used in real world (~5%). Nominal observations also have a set of values as its outcomes but those values do not have an order. Therefore, one has to annotate each possible value for nominal observations (and some ordinal observations). We use LOINC ``600-7`` "Bacteria identified in Blood by Culture" as an example. The outcome could be any kind of bacteria that can infect the blood. As an example, we annotate the following finding of *Staphylococcus aureus* in blood to HP: 0002726 "Recurrent Staphylococcus aureus infections", the best available HP term currently.::

  "coding":[
  {
    "system": "http://snomed.info/sct",
    "code": "3092008",
    "display": "Staphylococcus aureus"
  }
  ]

However, it is probably not realistic to annotate every possible bacteria to an HPO term. For one thing, it is a huge task; for the other thing, it requires a term exists in HPO that matches the bacteria, which is not the case. We can summarize our finding by saying that the identification of any bacteria in the blood indicates bacteremia. Therefore, we convert the problem of mapping N outcomes to mapping 2 outcomes ("presence" or "absence" of bacteria in blood). Physicians indeed interpretate ``600-7`` observations in this manner. Even if such observations do not have such interpretations, we can always create one automatically.

``Nar`` and other types
-----------------------
Other types of LOINC codes are much more heterogeneous, making their interpretation much more challenging. Since they only account for 1% of real world applications, we will not consider those LOINC codes for now. But in future, we may attempt to use those those resources with natural language processing, image analysis etc.

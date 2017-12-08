package org.monarchinitiative.loinc2hpo.framework;


/**
 * Instances of this enum are being used to express status of asynchronous or event-based actions such as successful
 * download of file, received network data, etc...
 * Created by Daniel Danis on 5/30/17.
 */
public enum Signal {
    DONE,
    CANCEL,
    FAILED
}

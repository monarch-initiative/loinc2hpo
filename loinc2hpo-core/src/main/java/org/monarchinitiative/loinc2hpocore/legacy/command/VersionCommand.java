package org.monarchinitiative.loinc2hpocore.legacy.command;


/**
 * For now, the class just retrieve version number from POM.
 * @TODO: implement the command interface.
 */
public class VersionCommand {

    public static String getVersion() {
        String version = "1.1.2";// default, should be overwritten by the following.
        try {
            Package p = VersionCommand.class.getPackage();
            version= p.getImplementationVersion();
        } catch (Exception e) {
            // do nothing
        }
        return version;
    }

}

package org.monarchinitiative.loinc2hpo.codesystems;

public class Code {
    private String system;
    private String code;
    private String display;
    private String definition;

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}

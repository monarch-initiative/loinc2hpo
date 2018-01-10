package org.monarchinitiative.loinc2hpo.util;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class HPO_Class_FoundTest {
    @Test
    public void toPattern() throws Exception {
        String key = "potassium:chloride symporter activity";
        //assertEquals(".*(\"potassium\").*", toPattern(key));
        Pattern pattern = Pattern.compile(".*(increase.*|decrease.*|elevate.*|reduc.*|high.*|low.*|above|below|abnormal.*).*");
        Matcher matcher = pattern.matcher(key.toLowerCase());
        assertEquals(false, matcher.matches());
        //assertEquals(false, key.toLowerCase().matches(".*\"potassium\".*"));
        pattern = Pattern.compile(toPattern("serum|blood|plasma"));
        matcher = pattern.matcher(key);
        assertEquals(false, matcher.matches());
        key = "potassium in blood";
        matcher = pattern.matcher(key);
        assertEquals(true, matcher.matches());
        key = "potassium in urin";
        matcher = pattern.matcher(key);
        assertEquals(false, matcher.matches());

    }

    public String toPattern(String key) {
        return ".*(" + key.toLowerCase() + ").*";
    }

}
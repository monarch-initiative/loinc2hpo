package org.monarchinitiative.loinc2hpogui.util;



import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;


public class HPO_Class_FoundTest {
    @Test
    public void toPattern() throws Exception {
        String key = "potassium:chloride symporter activity";
        //assertEquals(".*(\"potassium\").*", toPattern(key));
        Pattern pattern = Pattern.compile(".*(increase.*|decrease.*|elevate.*|reduc.*|high.*|low.*|above|below|abnormal.*).*");
        Matcher matcher = pattern.matcher(key.toLowerCase());
        assertFalse(matcher.matches());
        //assertEquals(false, key.toLowerCase().matches(".*\"potassium\".*"));
        pattern = Pattern.compile(toPattern("serum|blood|plasma"));
        matcher = pattern.matcher(key);
        assertFalse(matcher.matches());
        key = "potassium in blood";
        matcher = pattern.matcher(key);
        assertTrue(matcher.matches());
        key = "potassium in urin";
        matcher = pattern.matcher(key);
        assertFalse(matcher.matches());

    }


    public String toPattern(String key) {
        return ".*(" + key.toLowerCase() + ").*";
    }

}
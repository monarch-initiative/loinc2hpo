package org.monarchinitiative.loinc2hpo.io;

import org.junit.Test;

import static org.junit.Assert.*;

public class WriteToFileTest {
    @Test
    public void appendToFile() throws Exception {

        String path = "/Users/Aaron/savetest.txt";
        String content = "append a line from intellij";
        WriteToFile.appendToFile(content, path);
    }

}
package org.monarchinitiative.loinc2hpo.io;

import org.junit.Test;

import static org.junit.Assert.*;

public class WriteToFileTest {
    @Test
    public void appendToFile() throws Exception {

        String path = "/Users/Aaron/appendTotest.txt";
        String content = "append a line from intellij\n";
        WriteToFile.appendToFile(content, path);
        content = "this line is appended\n";
        WriteToFile.appendToFile(content, path);
    }

    @Test
    public void saveToFile() throws Exception {
        String path = "/Users/Aaron/saveTotest.txt";
        String content = "save a line from intelliJ\n";
        WriteToFile.writeToFile(content, path);


        content = "This should be the only line in the file\n";
        WriteToFile.writeToFile(content, path);
    }

}
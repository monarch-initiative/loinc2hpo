package org.monarchinitiative.loinc2hpogui.io;



import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.io.LoincOfInterest;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class LoincOfInterestTest {


    @Test
    public void testConstructor() {
        String path = getClass().getClassLoader().getResource
                ("list1.txt").getPath();
        try {
            LoincOfInterest loincOfInterest = new LoincOfInterest(path);
        } catch (FileNotFoundException e) {

            System.out.println("File not found");
        }
    }

    @Test
    public void testGetLoincOfInterest(){
        String path = getClass().getClassLoader().getResource
                ("list1.txt").getPath();
        try {
            LoincOfInterest loincOfInterest = new LoincOfInterest(path);
            assertEquals(4, loincOfInterest.getLoincOfInterest().size());
            for (String item: loincOfInterest.getLoincOfInterest()) {
                System.out.println(item);
            }
        } catch (FileNotFoundException e) {

            System.out.println("File not found");
        }
    }

    @Test
    public void testStripEN(){
        String a = "exocytosis@en";
        if (a.endsWith("@en")) {
            System.out.println(a.substring(0, a.length() - 3));
        }
        else {
            return;
        }
    }




}
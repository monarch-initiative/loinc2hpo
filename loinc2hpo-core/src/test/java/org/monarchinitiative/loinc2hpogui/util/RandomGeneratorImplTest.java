package org.monarchinitiative.loinc2hpogui.util;


import org.junit.jupiter.api.Test;
import org.monarchinitiative.loinc2hpocore.util.RandomGenerator;
import org.monarchinitiative.loinc2hpocore.util.RandomGeneratorImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class RandomGeneratorImplTest {
    RandomGenerator randomGenerator = new RandomGeneratorImpl();
    @Test
    public void randLowerCaseChar() throws Exception {
        int iter = 1000;
        for (int i = 0; i < iter; i++) {
            char c = randomGenerator.randLowerCaseChar();
            assertTrue(c >= 'a' && c <='z');
        }
    }

    @Test
    public void randUpperCaseChar() throws Exception {
        int iter = 1000;
        for (int i = 0; i < iter; i++) {
            char c = randomGenerator.randUpperCaseChar();
            assertTrue(c >= 'A' && c <='Z');
        }
    }

    @Test
    public void randChar() throws Exception {
        int iter = 1000;
        for (int i = 0; i < iter; i++) {
            char c = randomGenerator.randChar();
            assertTrue(c >= 'A' && c <='Z' || c >= 'a' && c <= 'z');
        }
    }

    @Test
    public void randLowerCaseChars() throws Exception {
        int iter = 1000;
        List<Character> chars = randomGenerator.randLowerCaseChars(iter);
        chars.forEach(c -> assertTrue(c >= 'a' && c <= 'z'));
    }

    @Test
    public void randUpperCaseChars() throws Exception {
        int iter = 1000;
        List<Character> chars = randomGenerator.randUpperCaseChars(iter);
        chars.forEach(c -> assertTrue(c >= 'A' && c <= 'Z'));
    }

    @Test
    public void randChars() throws Exception {
        int iter = 1000;
        List<Character> chars = randomGenerator.randChars(iter);
        assertEquals(iter, chars.size());
        chars.forEach(c -> assertTrue(c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z'));
    }

    @Test
    public void randInt() throws Exception {
        int iter = 1000;
        int low = 0;
        int high = 10;
        int r;
        for (int i = 0; i < iter; i++) {
            r = randomGenerator.randInt(low, high);
            assertTrue(r >= 0 && r <= 9);
        }
    }

    @Test
    public void randIntegers() throws Exception {
        int iter = 1000;
        int low = 0;
        int high = 10;
        List<Integer> ints = randomGenerator.randIntegers(low, high, iter);
        ints.forEach(i -> assertTrue(i >= 0 && i <= 9));
    }

    @Test
    public void randString() {
        int iter = 1000;
        int charNum = 5;
        int intNum = 5;
        for (int i = 0; i < iter; i++) {
            String string = randomGenerator.randString(charNum, intNum, false);
            assertEquals(charNum + intNum, string.length());
        }
    }

    @Test
    public void randStrings() {
        int iter = 1000;
        int charNum = 5;
        int intNum = 5;
        List<String> strings = randomGenerator.randStrings(iter, charNum, intNum, true);
        strings.forEach(s -> {
            assertEquals(charNum + intNum, s.length());
            assertTrue(Character.isAlphabetic(s.charAt(0)));
        });
    }

    @Test
    public void randDouble() {
        int iter = 1000;
        int low = 0;
        int high = 10;
        for (int i = 0; i < iter; i++) {
            double d = randomGenerator.randDouble(low, high);
            assertTrue(d >= low && d < high);
        }
    }

    @Test
    public void randDoubles() {
        int size = 1000;
        int low = 0;
        int high = 10;
        List<Double> list = randomGenerator.randDoubles(size, low, high);
        list.forEach(d -> assertTrue(d >= low && d < high));
    }

}
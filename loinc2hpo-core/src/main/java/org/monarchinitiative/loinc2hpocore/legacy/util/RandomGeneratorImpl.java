package org.monarchinitiative.loinc2hpocore.legacy.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class RandomGeneratorImpl implements RandomGenerator{

    private Random rand = new Random();


    @Override
    public char randLowerCaseChar() {

        return (char) (rand.nextInt(26) + 'a');

    }


    @Override
    public char randUpperCaseChar() {
        return (char) (rand.nextInt(26) + 'A');
    }


    @Override
    public char randChar() {
        boolean lowercase = rand.nextBoolean();
        if (lowercase) {
            return randLowerCaseChar();
        } else {
            return randUpperCaseChar();
        }
    }


    @Override
    public List<Character> randLowerCaseChars(int num) {
        if (num <= 0) throw new IllegalArgumentException();

        Character [] chars = new Character[num];
        for (int i = 0; i < num; i++) {
            chars[i] = randLowerCaseChar();
        }
        return Arrays.asList(chars);
    }


    @Override
    public List<Character> randUpperCaseChars(int num) {
        if (num <= 0) throw new IllegalArgumentException();

        Character [] chars = new Character[num];
        for (int i = 0; i < num; i++) {
            chars[i] = randUpperCaseChar();
        }
        return Arrays.asList(chars);
    }


    @Override
    public List<Character> randChars(int num) {
        if (num <= 0) throw new IllegalArgumentException();

        Character [] chars = new Character[num];
        for (int i = 0; i < num; i++) {
            chars[i] = randChar();
        }
        return Arrays.asList(chars);
    }


    @Override
    public int randInt(int lowBound, int upBound) {
        return rand.nextInt(upBound - lowBound) + lowBound;
    }


    @Override
    public List<Integer> randIntegers(int lowBount, int upBount, int length) {
        if (length <= 0) {
            throw new IllegalArgumentException();
        }

        Integer[] intList = new Integer[length];
        for (int i = 0; i < length; i++) {
            intList[i] = randInt(lowBount, upBount);
        }

        return Arrays.asList(intList);
    }


    @Override
    public String randString(int charCount, int intCount, boolean mustSeartWithAlph) {
        if (charCount <= 0 || intCount <= 0) {
            throw new IllegalArgumentException();
        }

        String string = randString(charCount, intCount);
        if (mustSeartWithAlph) {
            while (!Character.isAlphabetic(string.charAt(0))) {
                string = randString(charCount, intCount);
            }
        }
        return string;
    }


    private String randString(int charCount, int intCount) {
        List<Character> chars = randChars(charCount);
        List<Integer> ints = randIntegers(0, 10, intCount);
        List<String> all = new ArrayList<>();
        all.addAll(chars.stream().map(Object::toString).collect(Collectors.toList()));
        all.addAll(ints.stream().map(Object::toString).collect(Collectors.toList()));
        Collections.shuffle(all);
        return String.join("", all);
    }


    @Override
    public List<String> randStrings(int size, int alphNum, int digitNum, boolean mustStartWithAlph) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(randString(alphNum, digitNum, mustStartWithAlph));
        }
        return list;
    }


    @Override
    public double randDouble(double lowBound, double upBound) {
        return rand.nextDouble() * (upBound - lowBound) + lowBound;
    }


    @Override
    public List<Double> randDoubles(int size, double lowBound, double upBound) {
        List<Double> list = new ArrayList<>();
        DoubleStream stream = rand.doubles(size, lowBound, upBound);
        stream.forEach(list::add);
        return list;
    }

    @Override
    public boolean randBoolean() {
        return rand.nextBoolean();
    }
}

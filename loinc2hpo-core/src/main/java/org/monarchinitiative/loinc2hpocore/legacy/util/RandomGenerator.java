package org.monarchinitiative.loinc2hpocore.legacy.util;

import java.util.List;

public interface RandomGenerator {

    /**
     * Return a random lower case character
     * @return
     */
    char randLowerCaseChar();


    /**
     * Return a random upper case character
     * @return
     */
    char randUpperCaseChar();


    /**
     * Return a random character
     * @return
     */
    char randChar();


    /**
     * Return a list of random lower case characters
     * @param size, size of the list
     * @return
     */
    List<Character> randLowerCaseChars(int size);


    /**
     * Return a list of random upper case characters
     * @param size
     * @return
     */
    List<Character> randUpperCaseChars(int size);


    /**
     * Return a list of random characters, either lower or upper case
     * @param size
     * @return
     */
    List<Character> randChars(int size);


    /**
     * Return a random integer [lowBound, upBound)
     * @param lowBound
     * @param upBound
     * @return
     */
    int randInt(int lowBound, int upBound);


    /**
     * Return a list of random integers with bound [lowBound, upBound).
     * @param lowBound
     * @param upBound
     * @param length: size of list
     * @return
     */
    List<Integer> randIntegers(int lowBound, int upBound, int length);


    /**
     * Return a random string with specified number of characters and integers
     * @param alphNum, number of alphabets
     * @param digitNum, number of digits
     * @param mustSeartWithAlph, whether first character should be alphabet
     * @return
     */
    String randString(int alphNum, int digitNum, boolean mustSeartWithAlph);


    /**
     * Return a list of random strings with specified number of alphabets and digits
     * @param size
     * @param alphNum
     * @param digitNum
     * @param mustStartWithAlph
     * @return
     */
    List<String> randStrings(int size, int alphNum, int digitNum, boolean mustStartWithAlph);


    /**
     * Return a random double with the specified bounds
     * @param lowBound
     * @param upBound
     * @return
     */
    double randDouble(double lowBound, double upBound);


    /**
     * Return a list of random double with specified bounds
     * @param size
     * @param lowBound
     * @param upBound
     * @return
     */
    List<Double> randDoubles(int size, double lowBound, double upBound);


    /**
     * Return a random boolean
     * @return
     */
    boolean randBoolean();
}

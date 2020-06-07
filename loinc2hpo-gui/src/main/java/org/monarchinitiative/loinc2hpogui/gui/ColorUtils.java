package org.monarchinitiative.loinc2hpogui.gui;

import javafx.scene.paint.Color;

public class ColorUtils {

    final static String COLORVALUEFORMAT = "#%02X%02X%02X";

    public static String colorValue(int red, int green, int blue) {
        return String.format(COLORVALUEFORMAT, red, green, blue);
    }

    public static String colorValue(Color color) {
//        int red = (int) color.getRed() * 255;
//        int green = (int) color.getGreen() * 255;
//        int blue = (int) color.getBlue() * 255;
//        return colorValue(red, green, blue);
        return "#" + color.toString().substring(2,8).toUpperCase();
    }

}

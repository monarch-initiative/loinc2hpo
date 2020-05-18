package org.monarchinitiative.loinc2hpo.gui;


import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;


public class ColorUtilsTest {
    @Test
    public void colorValue() throws Exception {
    }

    @Test
    public void colorValue1() throws Exception {
        Color color = Color.AQUA;
        System.out.println(ColorUtils.colorValue(color));
    }

}
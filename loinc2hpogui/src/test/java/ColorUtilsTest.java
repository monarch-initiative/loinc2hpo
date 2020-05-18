import org.junit.Test;
import javafx.scene.paint.Color;
import org.monarchinitiative.loinc2hpo.gui.ColorUtils;

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
package org.monarchinitiative.loinc2hpo;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.monarchinitiative.loinc2hpo.framework.Injector;
import org.monarchinitiative.loinc2hpo.gui.loinc2hpomain.MainPresenter;
import org.monarchinitiative.loinc2hpo.gui.loinc2hpomain.MainView;
import org.monarchinitiative.loinc2hpo.model.Model;

public class Loinc2HpoFX extends Application {

    /**
     * A reference to the Model; we will write the current settings to file in
     * the {@link #stop} method by means of a method in the Model class.
     */
    private Model model;
    public static void main(String args[]) {
        launch(args);
    }

    public static final String APPLICATION_ICON = "img/vpvicon.png";

    private Stage primarystage = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.model=new Model();
        this.primarystage = primaryStage;
        // To-do -- make icon
       // Image image = new Image(Loinc2HpoFX.class.getResourceAsStream("/img/vpvicon.png"));
       // primaryStage.getIcons().add(image);
        primaryStage.setTitle("LOINC-2-HPO Biocuration Tool");
        // get dimensions of users screens to use as Maximum width/height
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        int xdim=(int)primScreenBounds.getWidth();
        int ydim=(int)primScreenBounds.getHeight();

        MainView view = new MainView();
        MainPresenter presenter = (MainPresenter) view.getPresenter();
        presenter.setModel(model);
        Scene scene = new Scene(view.getView());
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5),presenter.getRootPane());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);
        primarystage.setScene(scene);
        primarystage.show();

        fadeIn.play();
    }


    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }

}


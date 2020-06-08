package org.monarchinitiative.loinc2hpogui.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.monarchinitiative.loinc2hpogui.model.Settings;

/**
 * A helper class that displays the settings for this project
 * @author Peter Robinson
 * @version 0.1.3 (2017-11-12)
 */
public class SettingsViewFactory {

    private static String getHTML(Settings settings) {
        String sb = "<html><body>\n" +
                inlineCSS() +
                "<h1>LOINC2HPO Biocuration App Help</h1>" +
                "<p><i>Loinc2Hpo</i> settings:</p>" +
                setup(settings) +
                "</body></html>";
        return sb;

    }


    private static String inlineCSS() {
        return "<head><style>\n" +
                "  html { margin: 0; padding: 0; }" +
                "body { font: 100% georgia, sans-serif; line-height: 1.88889;color: #001f3f; margin: 10; padding: 10; }"+
                "p { margin-top: 0;text-align: justify;}"+
                "h2,h3 {font-family: 'serif';font-size: 1.4em;font-style: normal;font-weight: bold;"+
                "letter-spacing: 1px; margin-bottom: 0; color: #001f3f;}"+
                "  </style></head>";
    }

    private static String setup(Settings settings) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\nMandatory Settings:\n");
        sb.append(String.format("<li>Path to LOINC Core Table: %s</li>",settings.getLoincCoreTablePath()));
        sb.append(String.format("<li>Path to <tt>hp.obo</tt> file: %s</li>",settings.getHpoOboPath()));
        sb.append(String.format("<li>Path to <tt>hp.owl</tt> file: %s</li>",settings.getHpoOwlPath()));
        sb.append(String.format("<li>Path to annotation file: %s</li>",settings.getAnnotationFolder()));

        sb.append("\n\nOptional Settings:\n");
        sb.append(String.format("<li>Biocurator ID: %s</li>",settings.getBiocuratorID()));
        return String.format("<ul>%s</ul>",sb.toString());

    }






    /** Open a dialog that provides concise help for using PhenoteFX. */
    public static void openSettingsDialog(Settings settings) {
        Stage window;
        String windowTitle = "LOINC2HPO Biocuration App Settings";
        window = new Stage();
        window.setOnCloseRequest( event -> {window.close();} );
        window.setTitle(windowTitle);


        Pane pane = new Pane();
        VBox vbox =  new VBox();
        vbox.setPrefHeight(600);
        vbox.setPrefWidth(800);
        WebView wview = new WebView();
        wview.getEngine().loadContent(getHTML(settings));

        pane.getChildren().add(vbox);
        HBox hbox = new HBox();
        hbox.setPrefHeight(40);
        hbox.setPrefWidth(800);
        Region region=new Region();
        region.setPrefHeight(40);
        region.setPrefWidth(400);
        hbox.setHgrow(region, Priority.ALWAYS);
        Button button = new Button("Close");
        HBox.setMargin(button,new Insets(10, 10, 10, 0));
        button.setOnAction( e->window.close());
        hbox.getChildren().addAll(region,button);
        vbox.getChildren().addAll(wview,hbox);


        Scene scene = new Scene(pane, 800, 600);
        String css = SettingsViewFactory.class.getResource("/css/loinc2hpo.css").toExternalForm();
        scene.getStylesheets().add(css);
        window.setScene(scene);
        window.showAndWait();
    }

}

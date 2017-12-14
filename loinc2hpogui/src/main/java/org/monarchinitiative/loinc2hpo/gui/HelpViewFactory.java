package org.monarchinitiative.loinc2hpo.gui;

/*
 * #%L
 * HPhenote
 * %%
 * Copyright (C) 2017 Peter Robinson
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * A helper class that displays the Help in a JavaFX webview browser
 * @author Peter Robinson
 * @version 0.1.3 (2017-11-12)
 */
public class HelpViewFactory {
    private static final Logger logger = LogManager.getLogger();

    private static String getHTML() {
        String sb = "<html><body>\n" +
                inlineCSS() +
                "<h1>LOINC2HPO Biocuration App Help</h1>" +
                "<p><i>Loinc2Hpo</i> is designed to help curators create curations for LOINC code to HPO Term mappings.</p>" +
                setup() +
                openFile() +
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

    private static String setup() {
        return "<h2>Setup</h2>" +
                "<p>When you use Loinc2Hpo for the first time, you need to download some files and tell Loinc2Hpo" +
                "where you would like to store the annotation files. </p>" +
                "<p><ol><li><b>Loinc Core Table file</b> This is a file from Loinc that contains the names of Loinc codes " +
                "together with various information. Go to the LOINC download page (https://loinc.org/downloads/loinc/). You" +
                "will need to register. Download the LOINC Core Table file. Use the <tt>Edit|Set path to LOINC Core Table file</tt>" +
                " to tell Loinc2Hpo where you have stored this file.</li>" +
                "<li><b>Download HPO</b> This will download the latest release of <tt>hp.obo</tt> from the HPO GitHub page.</li>" +
                "<li><b>Set biocurator id</b> Enter whatever you would like be be nano-attributed by, e.g., MGM:rrabbit.</li>" +
                "<li><b>Show settings</b> This item opens a window to show the current settings.</li>" +
                "</ol></p>\n";
    }

    private static String openFile() {
        return "<h2>Working with LOINC</h2>" +
                "<p>For the simple case, there is one LOINC entry for a lab test that can be understood in isolation. " +
                "For instance, </p>\n";
    }




    /** Open a dialog that provides concise help for using PhenoteFX. */
    public static void openHelpDialog() {
        Stage window;
        String windowTitle = "LOINC2HPO Biocuration App Help";
        window = new Stage();
        window.setOnCloseRequest( event -> {window.close();} );
        window.setTitle(windowTitle);


        Pane pane = new Pane();
        VBox vbox =  new VBox();
        vbox.setPrefHeight(600);
        vbox.setPrefWidth(800);
        WebView wview = new WebView();
        wview.getEngine().loadContent(getHTML());

        pane.getChildren().add(vbox);
        HBox hbox = new HBox();
        hbox.setPrefHeight(30);
        hbox.setPrefWidth(800);
        Region region=new Region();
        region.setPrefHeight(30);
        region.setPrefWidth(400);
        hbox.setHgrow(region, Priority.ALWAYS);
        Button button = new Button("Close");
        button.setOnAction( e->window.close());
        hbox.getChildren().addAll(region,button);
        vbox.getChildren().addAll(wview,hbox);
        Scene scene = new Scene(pane, 800, 600);

        window.setScene(scene);
        window.showAndWait();
    }

}

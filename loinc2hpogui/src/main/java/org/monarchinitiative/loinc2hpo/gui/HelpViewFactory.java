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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;


/**
 * A helper class that displays the Help in a JavaFX webview browser
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.2.3 (2018-04-07)
 */
public class HelpViewFactory {
    private static final Logger logger = LogManager.getLogger();

    private static final String READTHEDOCS_SITE = "http://loinc2hpo.readthedocs.io/en/latest/";


    public static void openHelpDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("LOINC2HPO Help");
        alert.setHeaderText("Get help for the LOINC2HPO Biocuration App");
        alert.setContentText(String.format("A tutorial and detailed documentation for the LOINC2HPO Biocuration App can be found here online: %s",READTHEDOCS_SITE));

        ButtonType buttonTypeOne = new ButtonType("Open Help");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            openBrowser();
            alert.close();
        } else {
            alert.close();
        }
    }


    /**
     * Open a JavaFW Webview window and confirmDialog our read the docs help documentation in it.
     */
    private static void openBrowser() {
        try{
            Stage window;
            window = new Stage();
            WebView web = new WebView();
            web.getEngine().load(READTHEDOCS_SITE);
            Scene scene = new Scene(web);
            window.setScene(scene);
            window.show();
        } catch (Exception e){
            logger.error(String.format("Could not open browser to show RTD: %s",e.toString()));
            e.printStackTrace();
        }
    }
}

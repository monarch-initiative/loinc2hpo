package org.monarchinitiative.loinc2hpo.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FhirServerPopup {

    private static final Logger logger = LogManager.getLogger();
    private String base;

    public FhirServerPopup(String baseUrl) {
        this.base = baseUrl;
    }

    public void displayWindow() {
        Stage window = new Stage();
        window.setTitle("Server Settings");
        window.setResizable(false);
        window.centerOnScreen();
        window.initStyle(StageStyle.UTILITY);
        window.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(5);

        HBox baseUrlHBox = new HBox();
        baseUrlHBox.setSpacing(10);
        Label baseUrl = new Label("base URL");
        TextField baseUrlString = new TextField();
        if (this.base != null) {
            baseUrlString.setText(this.base);
        }
        baseUrlHBox.getChildren().addAll(baseUrl, baseUrlString);

        ComboBox<QueryMode> queryMode = new ComboBox();
        queryMode.getItems().addAll(QueryMode.values());

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        Label modeLabel = new Label("query mode");
        gridPane.add(modeLabel, 0, 0);

        gridPane.add(queryMode, 1, 0);

        Label resourceId = new Label("resource id");
        gridPane.add(resourceId, 0, 1);

        TextField textField = new TextField();
        textField.setPromptText("comma separated");
        gridPane.add(textField, 1, 1);

        Label identifier = new Label("identifier");
        gridPane.add(identifier, 0, 2);
        TextField system = new TextField("");
        system.setPromptText("system");
        gridPane.add(system, 1, 2);
        TextField id_identifier = new TextField();
        id_identifier.setPromptText("id");
        gridPane.add(id_identifier, 1, 3);

        Label name = new Label("name");
        gridPane.add(name, 0, 4);
        TextField firstName = new TextField();
        firstName.setPromptText("first name");
        gridPane.add(firstName, 1, 4);
        TextField lastName = new TextField();
        lastName.setPromptText("last name");
        gridPane.add(lastName, 1, 5);

        Label url = new Label("URL");
        gridPane.add(url, 0, 6);
        TextField urlField = new TextField();
        urlField.setPromptText("url");
        gridPane.add(urlField, 1, 6);

        HBox hBox = new HBox();
        Button cancel = new Button("Cancel");
        Button confirm = new Button("Confirm");
        hBox.setSpacing(20);
        hBox.getChildren().addAll(cancel, confirm);

        root.getChildren().addAll(baseUrlHBox, gridPane, hBox);
        Scene scene = new Scene(root, 300, 400);
        window.setScene(scene);

        cancel.setOnAction(p -> {
            logger.trace("user cancels");
            window.close();

        });

        confirm.setOnAction(c -> {
            logger.trace("user confirms");
            queryPatient(queryMode.getSelectionModel().getSelectedItem());
            window.close();
        });

        window.showAndWait();
    }

    enum QueryMode {
        RESOURCEID,
        IDENTIFIER,
        NAME,
        URL
    }

    void queryPatient(QueryMode mode) {
        //@TODO: implement
    }

}

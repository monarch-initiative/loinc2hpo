package org.monarchinitiative.loinc2hpo.gui;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A few convenience dialogs. TODO make nicer
 */
public class PopUps {
    private static final Logger logger = LogManager.getLogger();



    /**
     * Show information to user.
     *
     * @param text        - message text
     * @param windowTitle - Title of PopUp window
     */
    public static void showInfoMessage(String text, String windowTitle) {
        Alert al = new Alert(AlertType.INFORMATION);
        al.setTitle(windowTitle);
        al.setHeaderText(null);
        al.setContentText(text);
        al.showAndWait();
    }

    /**
     * Ask user to provide path to a File
     *
     * @param ownerWindow      - Stage with which the FileChooser will be associated
     * @param initialDirectory - Where to start the search
     * @param title            - Title of PopUp window
     * @return the selected file or null if no file was selected
     */
    @Deprecated // Use ScreensConfig instead
    public static File selectFileToOpen(Stage ownerWindow, File initialDirectory, String title) {
        final FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(initialDirectory);
        filechooser.setTitle(title);
        return filechooser.showOpenDialog(ownerWindow);
    }

    /**
     * Ask user to select path where he wants to save a File
     *
     * @param ownerWindow      Parent Stage object
     * @param initialDirectory Where to start the search
     * @param title            Title of PopUp window
     * @return the selected file or null if no file was selected
     */
    @Deprecated // Use ScreensConfig instead
    public static File selectFileToSave(Stage ownerWindow, File initialDirectory, String title, String initialFileName) {
        final FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(initialDirectory);
        filechooser.setInitialFileName(initialFileName);
        filechooser.setTitle(title);
        return filechooser.showSaveDialog(ownerWindow);
    }

    /**
     * Ask user to choose a directory
     *
     * @param ownerWindow      - Stage with which the DirectoryChooser will be associated
     * @param initialDirectory - Where to start the search
     * @param title            - Title of PopUp window
     * @return
     */
    @Deprecated // Use ScreensConfig instead
    public static File selectDirectory(Stage ownerWindow, File initialDirectory, String title) {
        final DirectoryChooser dirchooser = new DirectoryChooser();
        dirchooser.setInitialDirectory(initialDirectory);
        dirchooser.setTitle(title);
        return dirchooser.showDialog(ownerWindow);
    }

    /**
     * Request a String from user.
     *
     * @param windowTitle - Title of PopUp window
     * @param promptText  - Prompt of Text field (suggestion for user)
     * @param labelText   - Text of your request
     * @return String with user input
     */
    public static String getStringFromUser(String windowTitle, String promptText, String labelText) {
        TextInputDialog dialog = new TextInputDialog(promptText);
        dialog.setTitle(windowTitle);
        dialog.setHeaderText(null);
        dialog.setContentText(labelText);

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);

    }

    /**
     * Ask user a boolean question and get an answer.
     *
     * @param windowTitle Title of PopUp window
     * @return
     */
    public static boolean getBooleanFromUser(String question, String headerText, String windowTitle) {
        Alert al = new Alert(AlertType.CONFIRMATION);
        al.setTitle(windowTitle);
        al.setHeaderText(headerText);
        al.setContentText(question);

        Optional<ButtonType> result = al.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Present user a window with buttons
     *
     * @param choices     array of Strings, each string will be presented as button
     * @param labelText   text present in body of the popup window
     * @param windowTitle title of the popup window
     * @return {@link Optional} object containing String selected by user or empty if user selected cancel
     */
    public static Optional<String> getToggleChoiceFromUser(String[] choices, String labelText, String windowTitle) {
        Alert al = new Alert(AlertType.CONFIRMATION);

        al.setTitle(windowTitle);
        al.setHeaderText(null);
        al.setContentText(labelText);
        List<ButtonType> buttons = Arrays.stream(choices).map(ButtonType::new).collect(Collectors.toList());

        buttons.add(new ButtonType("Cancel", ButtonData.CANCEL_CLOSE));

        al.getButtonTypes().setAll(buttons);

        Optional<ButtonType> result = al.showAndWait();

        if (result.isPresent()) {
            ButtonType bt = result.get();
            if (bt.getButtonData() != ButtonData.CANCEL_CLOSE) {
                return Optional.of(bt.getText());
            }
        }
        return Optional.empty();
    }



    public static void showException(String windowTitle, String header, String contentText, Exception exception) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(windowTitle);
        alert.setHeaderText(header);
        alert.setContentText(contentText);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static void showWarningDialog(String windowTitle, String header, String contentText) {
        Alert a = new Alert(AlertType.WARNING);
        a.setTitle(windowTitle);
        a.setHeaderText(header);
        a.setContentText(contentText);
        a.showAndWait();
    }


    public static void showHtmlContent(String windowTitle, String resourcePath, Stage ownerWindow) {
        Stage window = getPopUpStage(windowTitle);
        Stage adjWindow = adjustStagePosition(window, ownerWindow);
        adjWindow.initStyle(StageStyle.DECORATED);
        adjWindow.setResizable(true);

        WebView browser = new WebView();
        WebEngine engine = browser.getEngine();
        engine.load(PopUps.class.getResource(resourcePath).toString());

        adjWindow.setScene(new Scene(browser));
        adjWindow.showAndWait();

    }


    private static Stage getPopUpStage(String title) {
        Stage window = new Stage();
        window.setResizable(false);
        window.centerOnScreen();
        window.setTitle(title);
        window.initStyle(StageStyle.UTILITY);
        window.initModality(Modality.APPLICATION_MODAL);
        return window;
    }


    /**
     * Ensure that popup Stage will be displayed on the same monitor as the parent Stage
     *
     * @param childStage
     * @param parentStage
     * @return
     */
    private static Stage adjustStagePosition(Stage childStage, Stage parentStage) {
        ObservableList<Screen> screensForParentWindow = Screen.getScreensForRectangle(parentStage.getX(), parentStage.getY(),
                parentStage.getWidth(), parentStage.getHeight());
        Screen actual = screensForParentWindow.get(0);
        Rectangle2D bounds = actual.getVisualBounds();

        // set top left position to 35%/25% of screen/monitor width & height
        childStage.setX(bounds.getWidth() * 0.35);
        childStage.setY(bounds.getHeight() * 0.25);
        return childStage;
    }


}

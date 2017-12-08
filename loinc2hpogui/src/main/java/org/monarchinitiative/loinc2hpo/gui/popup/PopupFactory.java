package org.monarchinitiative.loinc2hpo.gui.popup;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class PopupFactory {
    /** Indicates if the entry made by the user is valid and should be transmitted to the main controller.*/
    private boolean valid=false;

    private boolean wasCancelled=false;

    private Integer integerValue=null;

    private String stringValue=null;

    private static final String HTML_HEADER = "<html><head>%s</head><body>";
    private static final String HTML_FOOTER = "</body></html>";



    private boolean showDialogToGetIntegerFromUser(String title, String html, String labeltext, int previousValue, int defaultValue){
        Stage window;
        String windowTitle = title;
        window = new Stage();
        window.setOnCloseRequest( event -> {window.close();} );
        window.setTitle(windowTitle);

        PopupView view = new PopupView();
        PopupPresenter presenter = (PopupPresenter) view.getPresenter();
        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    window.close();
                    break;
                case CANCEL:
                case FAILED:
                    throw new IllegalArgumentException(String.format("Illegal signal %s received.", signal));
            }

        });
        presenter.setData(html);
        presenter.setLabelText(labeltext);
        if (previousValue>0) {
            presenter.setPreviousValue(String.valueOf(previousValue));
        } else {
            presenter.setPromptValue(String.valueOf(defaultValue));
        }

        window.setScene(new Scene(view.getView()));
        window.showAndWait();
        if (presenter.wasCanceled()) {
            wasCancelled=true;
            return false; // do nothing, the user canceled the entry
        }
        String entryvalue = presenter.getValue();
        try {
            this.integerValue=Integer.parseInt(entryvalue);
        } catch (NumberFormatException e) {
            displayException("Format error","Could not parse integer value",e);
            valid=false;
            return false;
        }
        return (! presenter.wasCanceled());
    }


    private boolean showDialogToGetStringFromUser(String title, String html, String labeltext, String previousValue, String defaultValue){
        Stage window;
        String windowTitle = title;
        window = new Stage();
        window.setOnCloseRequest( event -> {window.close();} );
        window.setTitle(windowTitle);

        PopupView view = new PopupView();
        PopupPresenter presenter = (PopupPresenter) view.getPresenter();
        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    window.close();
                    break;
                case CANCEL:
                case FAILED:
                    throw new IllegalArgumentException(String.format("Illegal signal %s received.", signal));
            }

        });
        presenter.setData(html);
        presenter.setLabelText(labeltext);
        if (previousValue!=null) {
            presenter.setPreviousValue(String.valueOf(previousValue));
        } else {
            presenter.setPromptValue(defaultValue);
        }


        window.setScene(new Scene(view.getView()));
        window.showAndWait();
        if (presenter.wasCanceled()) {
            wasCancelled=true;
            return false; // do nothing, the user canceled the entry
        }
        String value = presenter.getValue();
        if (value!=null && value.length()>0 ) {
            this.stringValue=value;
            valid=true;
        } else {
            valid=false;
        }

        return (! presenter.wasCanceled());
    }


    private static String getLOINCFIleHTML  () {
        String html = "<h1>LoincTableCore</h1>\n"+
                "<p>" +
                "This app visualizes information from the Loinc Table Core file to assist with " +
                "Loinc Code to HPO Term biocuration." +
                "</p>" +
                "<p>This file can be downlloaded from the Loinc website. Locate the downloaded" +
                "</p>";

        return html;
    }


    private static String getProbeLengthHTML() {
        String html = "<h1>Probe length</h1>\n"+
                "<p>The probes used in capture Hi-C are oligonucleotides (sometimes called baits) " +
                "that are used to capture sequences of interest, thereby enriching these sequences prior " +
                "to next generation sequencing. In some technologies, streptavidin-labeled magnetic beads " +
                "are used to capture target sequences in solution; in others, variable length probes are " +
                "attached to an array. The probe length entered here should match the probe length used in " +
                "the actual capture Hi-C experiment that will be conducted.</p>";

        return html;
    }

    private static String getProjectNameHTML() {
        String html = "<h1>VPV Projects</h1>\n"+
                "<p>Enter a name for a new VPV project. Names should start with letters, numbers, or an underscore." +
                " By default, VPV stores the projects in a hidden .vpvgui directory in the user's home directory." +
                " Projects can also be exported to other locations on the file system using the File|Export... menu item." +
                " Projects can be imported with Project|Import.</p>";

        return html;
    }


    //TODO delete or refacgtor
    public Integer setProbeLength(int previousValue) {
        String title="Enter Probe Length";
        String labelText="Enter probe length:";
        String html=getProbeLengthHTML();
        boolean OK=false;
        if (previousValue>0) {
            html=html +  "<p>The previously entered value is shown.</p>";
            html=String.format("%s%s%s",String.format(HTML_HEADER,getCSSblock()),html,HTML_FOOTER);
        } else {
            html=String.format("%s%s%s",String.format(HTML_HEADER,getCSSblock()),html,HTML_FOOTER);
        }
        OK = showDialogToGetIntegerFromUser(title,html,labelText,previousValue,22);

        if (OK) {
            return integerValue;
        } else {
            valid = false;
            return null;
        }
    }


    public Integer setMarginSize(int previousValue) {
        String title="Enter Margin Size";
        String labelText="Enter margin size (bp):";
        String html=getLOINCFIleHTML();
        boolean OK=false;
        if (previousValue>0) {
            html=html +  "<p>The previously entered value is shown.</p>";
            html=String.format("%s%s%s",String.format(HTML_HEADER,getCSSblock()),html,HTML_FOOTER);
        } else {
            html=String.format("%s%s%s",String.format(HTML_HEADER,getCSSblock()),html,HTML_FOOTER);
        }
        OK = showDialogToGetIntegerFromUser(title,html,labelText,previousValue,22);

        if (OK) {
            return integerValue;
        } else {
            valid = false;
            return null;
        }
    }

    /** Open up a dialog where the user can enter a new project name. */
    public String getProjectName() {
        String title="Enter New Project Name";
        String labelText="Enter project name:";
        String defaultProjectName="new project";
        String html=getProjectNameHTML();
        boolean OK=false;

        OK = showDialogToGetStringFromUser(title,html,labelText,null,defaultProjectName);

        if (OK) {
            return stringValue;
        } else {
            valid = false;
            return null;
        }

    }

    /**
     * Request a String from user.
     *
     * @param windowTitle - Title of PopUp window
     * @param promptText  - Prompt of Text field (suggestion for user)
     * @param labelText   - Text of your request
     * @return String with user input
     */
    @Deprecated
    public static String getStringFromUser(String windowTitle, String promptText, String labelText) {
        TextInputDialog dialog = new TextInputDialog(promptText);
        dialog.setTitle(windowTitle);
        dialog.setHeaderText(null);
        dialog.setContentText(labelText);
        Optional<String> result = dialog.showAndWait();

        return result.orElse(null);
    }


    public static void displayError(String title, String message) {

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        window.setMinWidth(250);
        Label label = new Label();
        label.setText(message);
        label.setStyle(
                "-fx-border-color: lightblue; "
                        + "-fx-font-size: 14;"
                        + "-fx-border-insets: -5; "
                        + "-fx-border-radius: 5;"
                        + "-fx-border-style: dotted;"
                        + "-fx-border-width: 2;"
                        + "-fx-alignment: top-left;"
                        + "-fx-text-fill: red;"
        );

        Button button = new Button("OK");

        button.setOnAction(e -> {
            window.close();
        });


        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 50, 50, 50));

        layout.getChildren().addAll(label, button);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }


    public static void displayMessage(String title, String message) {

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        window.setMinWidth(250);
        Label label = new Label();
        label.setText(message);
        label.setStyle(
                "-fx-border-color: lightblue; "
                        + "-fx-font-size: 14;"
                        + "-fx-border-insets: -5; "
                        + "-fx-border-radius: 5;"
                        + "-fx-border-style: dotted;"
                        + "-fx-border-width: 2;"
                        + "-fx-alignment: top-left;"
                        + "-fx-text-fill: blue;"
        );

        Button button = new Button("OK");

        button.setOnAction(e -> {
            window.close();
        });


        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 50, 50, 50));

        layout.getChildren().addAll(label, button);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }



    public static void displayException(String title, String message, Exception e) {
        TextArea textArea = new TextArea(e.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        Label label = new Label("The exception stacktrace was:");


        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText(title);
        alert.setContentText(message);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }




    public static void showAbout(String versionString, String dateString) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ViewPoint Viewer");
        alert.setHeaderText(null);
        alert.setContentText(String.format("Version %s\nLast changed: %s",versionString,dateString ));

        alert.showAndWait();
    }

    public static boolean confirmDialog(String title, String message) {
        final BooleanProperty answer = new SimpleBooleanProperty();
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        Label label = new Label();
        label.setText(message);

        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");

        yesButton.setOnAction(e -> {
            answer.setValue(true);
            window.close();
        });
        noButton.setOnAction(e -> {
            answer.setValue(false);
            window.close();
        });

        VBox layout = new VBox(10);

        layout.getChildren().addAll(label, yesButton, noButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return answer.getValue();
    }


    public boolean isValid() { return valid;}
    public boolean wasCancelled() { return wasCancelled; }


    /**
     * @return a block of CSS code intended for the blue-beige Table of data on the design.
     */
    public static String getCSSblock() {
        return "<style>\n" +
                "h1 {\n" +
                "\tfont-size: 16;\n" +
                "  font-weight: bold;\n" +
                "  color: #1C6EA4;\n" +
                "}\n" +
                "h2 {\n" +
                "\tfont-size: 16;\n" +
                "  font-weight: italic;\n" +
                "  color: #1C6EA4;\n" +
                "}\n" +
                "h4 {\n" +
                "\tfont-size: 12;\n" +
                "  font-weight: italic;\n" +
                "  color: #1C6EA4;\n" +
                "}\n" +
                "p.ex {\n" +
                "\tfont-size: 9;\n" +
                "}\n" +
                "</style>";
    }



    private static String getPreHTML(String text) {
       return String.format("<html><body><h1>VPV Report</h1><pre>%s</pre></body></html>",text);
    }

    public static  void showSummaryDialog(String text) {
        Stage window;
        String windowTitle = "VPV Report";
        window = new Stage();
        window.setOnCloseRequest( event -> {window.close();} );
        window.setTitle(windowTitle);

        PopupView view = new PopupView();
        PopupPresenter presenter = (PopupPresenter) view.getPresenter();
        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    window.close();
                    break;
                case CANCEL:
                case FAILED:
                    throw new IllegalArgumentException(String.format("Illegal signal %s received.", signal));
            }

        });
        presenter.setData(getPreHTML(text));
        presenter.hideButtons();


        window.setScene(new Scene(view.getView()));
        window.showAndWait();
    }



}

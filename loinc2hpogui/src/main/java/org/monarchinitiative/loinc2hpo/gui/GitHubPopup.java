package org.monarchinitiative.loinc2hpo.gui;

import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.ontology.data.TermSynonym;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.loinc2hpo.loinc.LoincEntry;
import org.monarchinitiative.loinc2hpo.loinc.LoincId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
public class GitHubPopup {

    private static final Logger logger = LogManager.getLogger();

    private LoincId loincId; //suggestions for the loinc code
    private LoincEntry loincEntry;

    private HpoTerm hpoTerm; //if the suggestion is about a known term
    private String uname = null;
    private String pword = null;
    /** Will be set to true if the user clicks the cancel button. */
    private boolean wasCancelled=false;
    /** GitHub labels the user can choose from. */
    private List<String> labels=new ArrayList<>();

    private String chosenLabel=null;

    /**
     * True if our new GitHub issue is to suggest a new child term for an existing HPO Term.
     */
    private boolean suggestNewChildTerm = false;
    /**
     * This is tbhe payload of the git issue we will create.
     */
    private String githubIssueText = null;
    private boolean newAnnotation = false;

    /**
     * Use this constructor to Suggest a new hpo term for a loinc code.
     */
    public GitHubPopup(LoincId loincId) {
        this.loincId = loincId;
    }

    public GitHubPopup(LoincEntry loincEntry) {
        this.loincEntry = loincEntry;
    }

    /**
     * Use this constructor to suggest a child term for a loinc code
     * @param term      An HPO Term for which we ant to suggest a new child term.
     * @param childterm set this to true if we want to create an issue to make a new child term
     */
    public GitHubPopup(HpoTerm term, LoincId loincId, boolean childterm) {
        this(loincId);
        this.hpoTerm = term;
        this.suggestNewChildTerm = childterm;
    }


    public void setLabels(List<String> labels){
        this.labels=labels;
    }


    public void displayWindow(Stage ownerWindow) {
        Stage window = new Stage();
        window.setResizable(false);
        window.centerOnScreen();
        window.setTitle("New github issue");
        window.initStyle(StageStyle.UTILITY);
        window.initModality(Modality.APPLICATION_MODAL);

        ObservableList<String> options = FXCollections.observableArrayList(labels);
        final ComboBox comboBox = new ComboBox(options);

        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(5);

        root.getChildren().add(new Label(String.format("Enter new GitHub issue for Loinc: %s", this.loincEntry.getLongName())));

        TextArea textArea = new TextArea();
        textArea.setText(getInitialText());
        root.getChildren().add(textArea);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {
            wasCancelled=true;
            window.close();
        });
        Button okButton = new Button("Create GitHub issue");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));


        Label userName = new Label("GitHub Username:");
        grid.add(userName, 0, 0);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 0);
        if (uname != null) {
            userTextField.setText(uname);
        }

        Label pw = new Label("GitHub Password:");
        grid.add(pw, 0, 1);
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 1);
        Label ghlabel = new Label("Label:");
        grid.add(ghlabel,0,2);
        grid.add(comboBox,1,2);

        okButton.setOnAction(e -> {
            githubIssueText = textArea.getText();
            uname = userTextField.getText();
            pword = pwBox.getText();
            if (comboBox.getSelectionModel().getSelectedItem()!=null) {
                String item = comboBox.getSelectionModel().getSelectedItem().toString();
                if (item!=null && !item.isEmpty()) {
                    this.chosenLabel=item;
                }
            }
            window.close();
        });
        if (pword != null) {
            pwBox.setText(pword);
        }
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.getChildren().addAll(cancelButton, okButton);

        root.getChildren().add(hbox);
        root.getChildren().add(grid);
        Scene scene = new Scene(root, 500, 400);

        window.setScene(scene);
        window.showAndWait();
    }

    /** This is used if the user has alreay entered their GitHub name and password (they are stored in the
     * {@link org.monarchinitiative.loinc2hpo.model.Model} object.
     * @param ghuname
     * @param ghpword
     */
    public void setupGithubUsernamePassword(String ghuname, String ghpword) {
        uname = ghuname;
        pword = ghpword;
    }


    public boolean wasCancelled() { return wasCancelled; }




    private String getInitialText() {
        if (suggestNewChildTerm) {
            return String.format("Suggest creating a new child term of %s [%s] for Loinc: %s\n" +
                    "New term label: %s\n" +
                    "New term comment (if any): %s\n" +
                    "Your biocurator ID for nanoattribution (if desired): %s", "?", "?", "?", "?", "?", "?");
        } else if (newAnnotation) {
            return String.format("Suggest creating a new term for Loinc %s [%s] \n" +
                            "new term label: %s\n" +
                            "New term comment (if any): %s\n" +
                            "Your biocurator ID for nanoattribution (if desired): %s\n",
                    "?", "?", "?", "?", "?");
        } else {
            return String.format("Suggestion");
        }
    }


    public String retrieveGitHubIssue() {
        return githubIssueText;
    }

    public String getGitHubUserName() {
        return uname;
    }

    public String getGitHubPassWord() {
        return pword;
    }

    public String getGitHubLabel() { return chosenLabel; }


    /**
     * Ensure that popup Stage will be displayed on the same monitor as the parent Stage
     *
     * @param childStage  reference to new window that will appear
     * @param parentStage reference to the primary stage
     * @return a new Stage to display a dialog.
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

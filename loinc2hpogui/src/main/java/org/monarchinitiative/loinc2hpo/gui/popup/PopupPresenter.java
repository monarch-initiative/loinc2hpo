package org.monarchinitiative.loinc2hpo.gui.popup;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.monarchinitiative.loinc2hpo.framework.Signal;


import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class PopupPresenter  implements Initializable  {
    @FXML
    AnchorPane apane;
    @FXML
    private WebView wview;

    @FXML private Button cancelButon;
    @FXML private Button okButton;
    @FXML private TextField entryTextField;
    @FXML private Label label;

    private String value=null;

    private boolean wasCanceled=true;

    private Consumer<Signal> signal;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setSignal(Consumer<Signal> signal) {
        this.signal = signal;
    }

    public void setData(String html) {
        WebEngine engine = wview.getEngine();
        engine.loadContent(html);
    }

    @FXML public void cancelButtonClicked(ActionEvent e) {
        e.consume();
        wasCanceled=true;
        signal.accept(Signal.DONE);
    }

    @FXML public void okButtonClicked(ActionEvent e) {
        e.consume();
        wasCanceled=false;
        value=this.entryTextField.getText().trim();
        signal.accept(Signal.DONE);
    }

    public boolean wasCanceled() { return  this.wasCanceled;}

    public String getValue(){ return this.value;}

    public void setPreviousValue(String value) {
        this.entryTextField.setText(value);
    }

    public void setPromptValue(String value) {
        this.entryTextField.setPromptText(value);
    }


    public void setLabelText(String labtext) {
        this.label.setText(labtext);
    }


    public void hideButtons() {
        this.apane.getChildren().removeAll();
        this.apane.getChildren().add(this.wview);
        HBox hbox = new HBox();
        hbox.getChildren().add(this.okButton);
    }

}

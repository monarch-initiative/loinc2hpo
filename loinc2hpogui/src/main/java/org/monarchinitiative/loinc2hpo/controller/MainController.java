package org.monarchinitiative.loinc2hpo.controller;

import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;


public class MainController {

    @FXML private  SetupTabController setupTabController;
    @FXML private  AnnotateTabController annotateTabController;


    @FXML
    MenuItem closeMenuItem;



    public void init() {}

    @FXML private void initialize() {
//        consoleTabController.injectMainController(this);
    }

}


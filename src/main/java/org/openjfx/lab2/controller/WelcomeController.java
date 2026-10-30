package org.openjfx.lab2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/** Controller of welcome.fxml - displays the welcome message. */
public class WelcomeController {

    @FXML private Label welcomeLabel;

    /** Sets the personalised welcome text. */
    public void setUsername(String name) {
        welcomeLabel.setText("Welcome, " + name + "!");
    }
}

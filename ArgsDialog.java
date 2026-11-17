package org.openjfx.lab2.auth;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.Optional;

/**
 * Shown before the login screen when n and t were missing or invalid on the
 * command line. Keeps asking until the user enters two whole numbers >= 1.
 * Runs on the JavaFX thread (it only shows UI, no file access or waiting on other threads).
 */
public final class ArgsDialog {

    private ArgsDialog() { }

    /** @return valid parameters, or empty if the user cancelled */
    public static Optional<PolicyArgs> ask() {
        String error = "The program arguments n and t are missing or invalid.";
        while (true) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Login policy");
            dialog.setHeaderText(error + "\nPlease enter them manually (whole numbers >= 1).");

            TextField nField = new TextField();
            nField.setPromptText("e.g. 3");
            TextField tField = new TextField();
            tField.setPromptText("e.g. 30");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.addRow(0, new Label("n (max wrong logins):"), nField);
            grid.addRow(1, new Label("t (block seconds):"), tField);
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> answer = dialog.showAndWait();
            if (answer.isEmpty() || answer.get() != ButtonType.OK) {
                return Optional.empty();
            }
            Integer n = PolicyArgs.parsePositive(nField.getText());
            Integer t = PolicyArgs.parsePositive(tField.getText());
            if (n != null && t != null) {
                return Optional.of(new PolicyArgs(n, t));
            }
            error = "Invalid values: \"" + nField.getText() + "\", \"" + tField.getText() + "\".";
        }
    }
}

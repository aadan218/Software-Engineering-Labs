module gcm.lab2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens gcm.lab2 to javafx.fxml;
    exports gcm.lab2;
}

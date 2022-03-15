module com.example.smartbird {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.smartbird to javafx.fxml;
    exports com.example.smartbird;
}
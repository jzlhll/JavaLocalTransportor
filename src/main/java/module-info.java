module com.allan.localnetworktransport {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.allan.localnetworktransport to javafx.fxml;
    exports com.allan.localnetworktransport;
}
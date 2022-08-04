module com.allan.localnetworktransport {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.allan.localnetworktransport to javafx.fxml;
    exports com.allan.localnetworktransport;
    exports com.allan.localnetworktransport.bean;
    opens com.allan.localnetworktransport.bean to javafx.fxml;
    exports com.allan.localnetworktransport.arch;
    opens com.allan.localnetworktransport.arch to javafx.fxml;
}
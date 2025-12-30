module com.mycompany.clientxo {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.clientxo to javafx.fxml;
    exports com.mycompany.clientxo;
}

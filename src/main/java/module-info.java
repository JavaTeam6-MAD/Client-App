module com.mycompany.clientxo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.mycompany.clientxo to javafx.fxml;
    opens com.mycompany.clientxo.presentation.auth to javafx.fxml;
    opens com.mycompany.clientxo.presentation.difficultyscreen to javafx.fxml;
    opens com.mycompany.clientxo.presentation.game to javafx.fxml;
    opens com.mycompany.clientxo.presentation.gamehistory to javafx.fxml;
    opens com.mycompany.clientxo.presentation.homescreen to javafx.fxml;
    opens com.mycompany.clientxo.presentation.lobbyscreen to javafx.fxml;
    opens com.mycompany.clientxo.presentation.localmultiplayergame to javafx.fxml;
    opens com.mycompany.clientxo.presentation.localplayersnamedialog to javafx.fxml;
    opens com.mycompany.clientxo.presentation.profile to javafx.fxml;

    exports com.mycompany.clientxo;
}

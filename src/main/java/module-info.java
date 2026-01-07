module com.mycompany {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;

    opens com.mycompany to javafx.fxml;
    opens com.mycompany.presentation.auth to javafx.fxml;
    opens com.mycompany.presentation.difficultyscreen to javafx.fxml;
    opens com.mycompany.presentation.game to javafx.fxml;
    opens com.mycompany.presentation.gamehistory to javafx.fxml;
    opens com.mycompany.presentation.homescreen to javafx.fxml;
    opens com.mycompany.presentation.lobbyscreen to javafx.fxml;
    opens com.mycompany.presentation.localmultiplayergame to javafx.fxml;
    opens com.mycompany.presentation.localplayersnamedialog to javafx.fxml;
    opens com.mycompany.presentation.profile to javafx.fxml;

    exports com.mycompany;
}

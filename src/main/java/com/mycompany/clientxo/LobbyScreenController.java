package com.mycompany.clientxo;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class LobbyScreenController implements Initializable {

    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserScore;
    @FXML
    private Label lblUserChar;
    @FXML
    private Button btnTabFriends;
    @FXML
    private Button btnTabLeaderboard;
    @FXML
    private HBox friendsHeader;
    @FXML
    private Label lblOnlineCount;
    @FXML
    private Label lblOfflineCount;
    @FXML
    private VBox listContainer;

    private String currentView = "FRIENDS"; // FRIENDS or LEADERBOARD
    private String pendingChallengeId = null;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void onTabFriends() {
        
    }

    @FXML
    private void onTabLeaderboard() {
       
    }

  
    @FXML
    private void onProfile() {
        try {
            App.setRoot("ProfileScreen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRecordedGames() {
        // Mock Recorded Games List
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Recorded Games");
        alert.setHeaderText("Your Match History");
        alert.setContentText("1. vs CyberNinja (Win)\n2. vs PixelMaster (Loss)\n(No other games found)");
        alert.show();
    }

    @FXML
    private void onBack() {
        try {
            App.setRoot("primary");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCharacterSymbol(String charId) {
        if (charId == null)
            return "👤";
        switch (charId) {
            case "dragon":
                return "🐲";
            case "robot":
                return "🤖";
            case "alien":
                return "👽";
            case "ghost":
                return "👻";
            default:
                return "👤";
        }
    }
}

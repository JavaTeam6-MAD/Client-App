package com.mycompany.clientxo.presentation.localmultiplayergame;

import com.mycompany.clientxo.App;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LocalMultiplayerGameController {

    @FXML
    private Label playerXName;
    @FXML
    private Label playerOName;
    @FXML
    private Label scoreX;
    @FXML
    private Label scoreO;
    @FXML
    private Label statusText;
    @FXML
    private GridPane gameGrid;
    @FXML
    private Button playAgainButton;
    @FXML
    private Button homeButton;
    
    // SVG Paths
    private static final String PATH_X = "M10,10 L90,90 M90,10 L10,90";
    private static final String PATH_O = "M50,10 A40,40 0 1,1 50,90 A40,40 0 1,1 50,10";

    private LocalGame game = new LocalGame();
    
    public void setPlayerNames(String nameX, String nameO) {
        playerXName.setText(nameX.isEmpty() ? "Player X" : nameX);
        playerOName.setText(nameO.isEmpty() ? "Player O" : nameO);
    }

    @FXML
    public void initialize() {

    }

    @FXML
    private void handleGridClick(ActionEvent event) {
        Button button = (Button) event.getSource();
        
        Integer r = GridPane.getRowIndex(button);
        Integer c = GridPane.getColumnIndex(button);
        int row,col;
        if(r == null) {
            row = 0;
        } else {
            row = r;
        }
        if(c == null) {
            col = 0;
        } else {
            col = c;
        }
        
        if(game.makeMove(row, col)) {
            char currentPlayer = game.getCurrentPlayer();
            drawSymbol(button, currentPlayer);
            
            if(game.chechWinner()) {
                game.setGameActive(false);
                game.incrementScore();
                updateUI();
                statusText.setText("Winner: " + (currentPlayer == 'X' ? playerXName.getText() : playerOName.getText()));
            } else if(game.isBoardFull()) {
                game.setGameActive(false);
                statusText.setText("It's a Draw!");
            } else {
                game.nextTurn();
                statusText.setText("Turn: " + (game.getCurrentPlayer() == 'X' ? playerXName.getText() : playerOName.getText()));
            }
        } 
    }
    
    private void drawSymbol(Button button, char symbol) {
        SVGPath path = new SVGPath();
        path.setContent(symbol == 'X' ? PATH_X : PATH_O);
        path.setStyle("-fx-stroke: " + (symbol == 'X' ? "#FF4B2B" : "#2193b0") + "; -fx-stroke-width: 5;");
        button.setGraphic(path);
    }
    
    private void updateUI() {
        scoreX.setText(String.valueOf(game.getScoreX()));
        scoreO.setText(String.valueOf(game.getScoreO()));
    }

    @FXML
    private void resetGame(ActionEvent event) {
        game.resetGame();
        statusText.setText("Turn: " + playerXName.getText());
        for (Node node : gameGrid.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setGraphic(null);
            }
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            App.setRoot("primary");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onHome(ActionEvent event) {
        try {
            App.setRoot("primary");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateButton(Button btn) {
        if (btn.getGraphic() != null) {
            ScaleTransition st = new ScaleTransition(Duration.millis(300), btn.getGraphic());
            st.setFromX(0.1);
            st.setFromY(0.1);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        }
    }
}

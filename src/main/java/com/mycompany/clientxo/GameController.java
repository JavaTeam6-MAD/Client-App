package com.mycompany.clientxo;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameController {

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

    private Button[] buttons = new Button[9];
    private String[] board = new String[9];
    private boolean isGameActive = true;
    private String currentPlayer = "X";
    private String winner = null;
    private int scoreXVal = 0;
    private int scoreOVal = 0;
    private String mode = "computer";
    private String difficulty = "medium"; 

    private final int[][] WINNING_COMBINATIONS = {
            { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 },
            { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 },
            { 0, 4, 8 }, { 2, 4, 6 }
    };

    @FXML
    public void initialize() {
        
        for (Node node : gameGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                if (btn.getUserData() != null) {
                    int index = Integer.parseInt((String) btn.getUserData());
                    buttons[index] = btn;
                }
            }
        }
        resetGame();
    }

    public void setGameMode(String mode, String difficulty) {
        
    }

    private void updatePlayerNames() {
       
    }

    @FXML
    private void handleGridClick(ActionEvent event) {
        
    }

    private void makeMove(int index, String player) {
        
    }

    private void makeComputerMove() {
     
    }

    private int getComputerMoveLogic() {
        
        return 0;
      
    }

    private void checkGameState() {
     
    }

    private void endGame(String winner, int[] line) {
        
    }

    private void highlightWinningLine(int[] line, String winner) {
        
    }

    private void updateStatusText() {
    
    }

    @FXML
    public void resetGame() {
       
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        
        
    }

    private void animateButton(Button btn) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
        st.setFromX(0.5);
        st.setFromY(0.5);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }
}

package com.mycompany.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.model.app.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GameRecorder {

    private static final String RECORDINGS_DIR = "recorded_games";
    private RecordedGame currentGame;

    public void startGame(String gameId, String player1Name, String player2Name, String player1Symbol,
            String player2Symbol) {
        currentGame = new RecordedGame();
        currentGame.setGameId(gameId);
        currentGame.setPlayer1Name(player1Name);
        currentGame.setPlayer2Name(player2Name);
        currentGame.setPlayer1Symbol(player1Symbol);
        currentGame.setPlayer2Symbol(player2Symbol);
        currentGame.setDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        currentGame.setMoves(new ArrayList<>());
    }

    public void recordMove(String playerName, int row, int col, String symbol) {
        if (currentGame != null) {
            currentGame.getMoves().add(new GameMove(playerName, row, col, symbol));
        }
    }

    public void endGame(String winnerName, boolean isDraw) {
        if (currentGame != null) {
            currentGame.setWinnerName(winnerName);
            currentGame.setDraw(isDraw);
            saveGame();
        }
    }

    public static List<RecordedGame> getRecordedGames() {
        List<RecordedGame> games = new ArrayList<>();
        File dir = new File(RECORDINGS_DIR);
        if (!dir.exists()) {
            return games;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files != null) {
            Gson gson = new Gson();
            for (File file : files) {
                try {
                    String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
                    RecordedGame game = gson.fromJson(content, RecordedGame.class);
                    if (game != null) {
                        games.add(game);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // Sort by date desc
        games.sort((g1, g2) -> g2.getDate().compareTo(g1.getDate()));
        return games;
    }

    private void saveGame() {
        try {
            File dir = new File(RECORDINGS_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(currentGame);
            String filename = RECORDINGS_DIR + File.separator + "game_" + currentGame.getGameId() + ".json";

            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(json);
                System.out.println("Game recorded to: " + filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Inner Classes for Data Structure ---

    public static class RecordedGame implements Serializable {
        private String gameId;
        private String date;
        private String player1Name;
        private String player2Name;
        private String player1Symbol;
        private String player2Symbol;
        private List<GameMove> moves;
        private String winnerName;
        private boolean isDraw;

        // Getters and Setters
        public String getGameId() {
            return gameId;
        }

        public void setGameId(String gameId) {
            this.gameId = gameId;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getPlayer1Name() {
            return player1Name;
        }

        public void setPlayer1Name(String player1Name) {
            this.player1Name = player1Name;
        }

        public String getPlayer2Name() {
            return player2Name;
        }

        public void setPlayer2Name(String player2Name) {
            this.player2Name = player2Name;
        }

        public String getPlayer1Symbol() {
            return player1Symbol;
        }

        public void setPlayer1Symbol(String player1Symbol) {
            this.player1Symbol = player1Symbol;
        }

        public String getPlayer2Symbol() {
            return player2Symbol;
        }

        public void setPlayer2Symbol(String player2Symbol) {
            this.player2Symbol = player2Symbol;
        }

        public List<GameMove> getMoves() {
            return moves;
        }

        public void setMoves(List<GameMove> moves) {
            this.moves = moves;
        }

        public String getWinnerName() {
            return winnerName;
        }

        public void setWinnerName(String winnerName) {
            this.winnerName = winnerName;
        }

        public boolean isDraw() {
            return isDraw;
        }

        public void setDraw(boolean draw) {
            isDraw = draw;
        }
    }

    public static class GameMove implements Serializable {
        private String playerName;
        private int row;
        private int col;
        private String symbol;

        public GameMove(String playerName, int row, int col, String symbol) {
            this.playerName = playerName;
            this.row = row;
            this.col = col;
            this.symbol = symbol;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public String getSymbol() {
            return symbol;
        }
    }
}

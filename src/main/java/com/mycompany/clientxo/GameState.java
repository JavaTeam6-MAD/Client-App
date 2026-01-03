package com.mycompany.clientxo;

public class GameState {

    public enum GameMode {
        SINGLE_PLAYER,
        LOCAL_MULTIPLAYER,
        NETWORK_MULTIPLAYER
    }

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    private static GameState instance;

    // Game Configuration
    private GameMode currentMode;
    private Difficulty currentDifficulty;

    // User Data (Network)
    private String username;
    private String characterId; // "dragon", "robot", "alien", "ghost"
    private int score;

    private GameState() {
        // Defaults
        currentMode = GameMode.SINGLE_PLAYER;
        currentDifficulty = Difficulty.EASY;
        username = "Guest";
        characterId = "robot";
        score = 0;
    }

    public static synchronized GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    // Getters and Setters
    public GameMode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(GameMode currentMode) {
        this.currentMode = currentMode;
    }

    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(Difficulty currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

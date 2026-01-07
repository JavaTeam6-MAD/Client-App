package com.mycompany.core.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * SoundManager - Handles all audio playback for the XO game
 * Singleton pattern for global access throughout the application
 */
public class SoundManager {

    private static SoundManager instance;
    private Map<String, MediaPlayer> soundEffects;
    private boolean soundEnabled = true;
    private double soundVolume = 0.6;

    // Sound effect keys
    public static final String BUTTON_CLICK = "button_click";
    public static final String PLACE_X = "place_x";
    public static final String PLACE_O = "place_o";
    public static final String WIN = "win";
    public static final String LOSE = "lose";
    public static final String DRAW = "draw";

    private SoundManager() {
        soundEffects = new HashMap<>();
        loadSounds();
    }

    /**
     * Get the singleton instance of SoundManager
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /**
     * Load all sound effects into memory
     */
    private void loadSounds() {
        try {
            loadSound(BUTTON_CLICK, "/com/mycompany/sounds/button_click.wav");
            loadSound(PLACE_X, "/com/mycompany/sounds/place_x.wav");
            loadSound(PLACE_O, "/com/mycompany/sounds/place_o.wav");
            loadSound(WIN, "/com/mycompany/sounds/win.wav");
            loadSound(LOSE, "/com/mycompany/sounds/lose.wav");
            loadSound(DRAW, "/com/mycompany/sounds/draw.wav");
        } catch (Exception e) {
            System.err.println("Error loading sounds: " + e.getMessage());
        }
    }

    /**
     * Load a single sound effect
     */
    private void loadSound(String key, String resourcePath) {
        try {
            URL resource = getClass().getResource(resourcePath);
            if (resource != null) {
                Media sound = new Media(resource.toString());
                MediaPlayer player = new MediaPlayer(sound);
                player.setVolume(soundVolume);
                soundEffects.put(key, player);
            } else {
                System.err.println("Sound file not found: " + resourcePath);
            }
        } catch (Exception e) {
            System.err.println("Error loading sound " + key + ": " + e.getMessage());
        }
    }

    /**
     * Play a sound effect
     */
    public void playSound(String soundKey) {
        if (!soundEnabled)
            return;

        try {
            MediaPlayer player = soundEffects.get(soundKey);
            if (player != null) {
                player.stop();
                player.seek(Duration.ZERO);
                player.play();
            }
        } catch (Exception e) {
            System.err.println("Error playing sound " + soundKey + ": " + e.getMessage());
        }
    }

    /**
     * Set sound effects volume (0.0 to 1.0)
     */
    public void setSoundVolume(double volume) {
        this.soundVolume = Math.max(0.0, Math.min(1.0, volume));
        soundEffects.values().forEach(player -> player.setVolume(soundVolume));
    }

    /**
     * Enable or disable sound effects
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    /**
     * Check if sounds are enabled
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Cleanup resources when application closes
     */
    public void cleanup() {
        soundEffects.values().forEach(MediaPlayer::dispose);
    }
}

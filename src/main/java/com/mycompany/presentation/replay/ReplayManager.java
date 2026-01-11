package com.mycompany.presentation.replay;

import com.mycompany.presentation.networkgame.GameRecorder;

public class ReplayManager {
    private static ReplayManager instance;
    private GameRecorder.RecordedGame gameToReplay;

    private ReplayManager() {
    }

    public static ReplayManager getInstance() {
        if (instance == null) {
            instance = new ReplayManager();
        }
        return instance;
    }

    public void setGameToReplay(GameRecorder.RecordedGame game) {
        this.gameToReplay = game;
    }

    public GameRecorder.RecordedGame getGameToReplay() {
        return gameToReplay;
    }
}

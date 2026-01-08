package com.mycompany.presentation.gamehistory;

import com.mycompany.data.repo_impl.GameRepositoryImpl;
import com.mycompany.data.repo_interface.GameRepository;
import com.mycompany.model.app.RecordedGame;

import java.util.List;

public class GameHistoryManager {
    private final GameRepository gameRepository;

    public GameHistoryManager() {
        this.gameRepository = new GameRepositoryImpl();
    }

    /**
     * Fetches game history for a specific player
     * 
     * @param playerId The ID of the player
     * @return List of RecordedGame objects
     */
    public List<RecordedGame> getGameHistory(int playerId) {
        return gameRepository.getGameHistory(playerId);
    }
}

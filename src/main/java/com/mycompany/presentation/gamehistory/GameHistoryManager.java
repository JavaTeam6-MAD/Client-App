package com.mycompany.presentation.gamehistory;

import com.mycompany.data.repo_impl.GameRepositoryImpl;
import com.mycompany.data.repo_interface.GameRepository;
import com.mycompany.model.app.Game;

import java.util.List;

public class GameHistoryManager {
    private final GameRepository gameRepository;

    public GameHistoryManager() {
        this.gameRepository = new GameRepositoryImpl();
    }

    /**
     * Fetches the game history for the currently logged-in player
     * 
     * @return List of games
     */
    public List<Game> getGameHistory() {
        return gameRepository.getGameHistory();
    }
}

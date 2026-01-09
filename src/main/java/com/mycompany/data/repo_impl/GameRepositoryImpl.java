package com.mycompany.data.repo_impl;

import com.mycompany.data.datasource.remote.RemoteDataSource;
import com.mycompany.data.repo_interface.GameRepository;
import com.mycompany.data.repo_interface.PlayerRepository;
import com.mycompany.model.app.Game;
import com.mycompany.model.app.Player;
import com.mycompany.model.app.RecordedGame;

import java.util.List;

public class GameRepositoryImpl implements GameRepository {
    private final RemoteDataSource remoteDataSource;
    private final PlayerRepository playerRepository;

    public GameRepositoryImpl() {
        this.remoteDataSource = new RemoteDataSource();
        this.playerRepository = new PlayerRepositoryImpl();
    }

    @Override
    public List<Game> getGameHistory() {
        Player currentPlayer = playerRepository.getCurrentPlayer();
        if (currentPlayer == null || currentPlayer.getId() == 0) {
            throw new IllegalStateException("No player is currently logged in");
        }
        return remoteDataSource.getGameHistory(currentPlayer.getId());
    }

    @Override
    public RecordedGame getRecordedGame(int gameId) {
        // Placeholder for future implementation
        // Will be used to load recorded game moves from files
        throw new UnsupportedOperationException("Not implemented yet - will load from files");
    }
}

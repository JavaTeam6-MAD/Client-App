package com.mycompany.data.repo_impl;

import com.mycompany.data.datasource.remote.RemoteDataSource;
import com.mycompany.data.repo_interface.GameRepository;
import com.mycompany.model.app.RecordedGame;
import java.util.List;

public class GameRepositoryImpl implements GameRepository {
    private final RemoteDataSource remoteDataSource;

    public GameRepositoryImpl() {
        this.remoteDataSource = new RemoteDataSource();
    }

    @Override
    public List<RecordedGame> getGameHistory(int playerId) {
        return remoteDataSource.getGameHistory(playerId);
    }
}

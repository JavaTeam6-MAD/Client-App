package com.mycompany.data.repo_impl;

import com.mycompany.data.datasource.local.PlayerDAO;
import com.mycompany.data.datasource.remote.RemoteDataSource;
import com.mycompany.data.repo_interface.GameRepository;
import com.mycompany.model.app.RecordedGame;

public class GameRepositoryImpl implements GameRepository {
    private final RemoteDataSource remoteDataSource;
        private final PlayerDAO playerDAO;


    public GameRepositoryImpl() {
        this.remoteDataSource = RemoteDataSource.getInstance();
        this.playerDAO = new PlayerDAO();
    }
    
    @Override
    public void getGameHistory() {
        // Change: Removed the 'return' and matched the 'void' return type
        remoteDataSource.getGameHistory(playerDAO.get().getId());
    }
    @Override
    public RecordedGame getRecordedGame(int gameId) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
}

package com.mycompany.presentation.gamehistory;

import com.mycompany.data.datasource.remote.RemoteDataSource;
import com.mycompany.data.repo_interface.GameRepository;
import com.mycompany.data.repo_impl.GameRepositoryImpl;
import com.mycompany.data.repo_impl.PlayerRepositoryImpl;
import com.mycompany.data.repo_interface.PlayerRepository;
import com.mycompany.model.app.Game;
import com.mycompany.model.app.Player;
import java.util.List;
import javafx.application.Platform;

public class GameHistoryManager {
    private GameHistoryListener listener;
    private GameRepository repository;
    private RemoteDataSource remoteDataSource;
    private PlayerRepository playerRepository;

    public GameHistoryManager() {
        this.repository = new GameRepositoryImpl();
        this.remoteDataSource = RemoteDataSource.getInstance();
        remoteDataSource.setGameHistoryManager(this);
        this.playerRepository = new PlayerRepositoryImpl();
    }

    public void setListener(GameHistoryListener listener) {
        this.listener = listener;
    }

    public void loadGameHistory() {
        if (RemoteDataSource.getInstance().getLobbyManager() != null &&
                RemoteDataSource.getInstance().getLobbyManager().getCurrentPlayer() != null) {

            int userId = RemoteDataSource.getInstance().getLobbyManager().getCurrentPlayer().getId();
            repository.getGameHistory();
        } else {
            System.err.println("Cannot load game history: No User Logged in");
        }
    }
    
    public Player getCurrentPlayer(){
    return playerRepository.getCurrentPlayer();
    }

    public void onGameHistoryReceived(List<Game> games) {
        if (listener != null) {
            Platform.runLater(() -> {
                listener.onDataLoaded(games);
            });
        }
    }

    public void onFailure(String msg) {
        System.out.println("Game History Error: " + msg);
    }

    public void detach() {
        remoteDataSource.detachGameHistoryManager();
    }
}

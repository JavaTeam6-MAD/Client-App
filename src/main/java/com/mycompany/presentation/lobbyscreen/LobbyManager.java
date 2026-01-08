package com.mycompany.presentation.lobbyscreen;

import com.mycompany.data.repo_impl.PlayerRepositoryImpl;
import com.mycompany.data.repo_interface.PlayerRepository;
import com.mycompany.model.app.Player;

import java.util.List;

public class LobbyManager {
    private final PlayerRepository playerRepository;

    public LobbyManager() {
        this.playerRepository = new PlayerRepositoryImpl();
    }

    public Player getCurrentPlayer() {
        return playerRepository.getCurrentPlayer();
    }

    public void logout() {
        playerRepository.logout();
    }

    public List<Player> getFriends() {
        return playerRepository.getFriends();
    }
    public void leaveLobby() {
        playerRepository.setPlayerUnavailable();
    }
}

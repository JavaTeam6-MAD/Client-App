package com.mycompany.presentation.homescreen;

import com.mycompany.data.repo_impl.PlayerRepositoryImpl;
import com.mycompany.data.repo_interface.PlayerRepository;
import com.mycompany.model.app.Player;

public class HomeManager {
    private final PlayerRepository playerRepository;

    public HomeManager() {
        this.playerRepository = new PlayerRepositoryImpl();
    }

    public Player getCurrentPlayer() {
        return playerRepository.getCurrentPlayer();
    }
}

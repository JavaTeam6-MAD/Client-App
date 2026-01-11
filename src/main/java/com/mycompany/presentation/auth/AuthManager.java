package com.mycompany.presentation.auth;

import com.mycompany.data.repo_impl.PlayerRepositoryImpl;
import com.mycompany.data.repo_interface.PlayerRepository;
import com.mycompany.model.app.Player;

public class AuthManager {
    private final PlayerRepository playerRepository;

    public AuthManager() {
        this.playerRepository = new PlayerRepositoryImpl();
    }

    public Player login(String username, String password) {
        Player player = playerRepository.login(username, password);
        System.out.println(player.getUserName() + player.getPassword());
        return player;
    }

    public Player register(String username, String password) {
        Player player = playerRepository.register(username, password);
        System.out.println(player.getUserName() + player.getPassword());
        return player;
    }

    public Player getCurrentPlayer() {
        return playerRepository.getCurrentPlayer();
    }
}

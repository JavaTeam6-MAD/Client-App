package com.mycompany.data.repo_impl;

import com.mycompany.data.datasource.local.PlayerDAO;
import com.mycompany.data.datasource.remote.RemoteDataSource;
import com.mycompany.data.repo_interface.PlayerRepository;
import com.mycompany.model.app.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerRepositoryImpl implements PlayerRepository {
    private final RemoteDataSource remoteDataSource;
    private final PlayerDAO playerDAO;

    public PlayerRepositoryImpl() {
        this.remoteDataSource = new RemoteDataSource();
        this.playerDAO = new PlayerDAO();
    }

    @Override
    public Player login(String name, String password) {
        Player player = remoteDataSource.login(name, password);
        if (player.getId() != 0) {
            playerDAO.save(player);
        }
        return player;
    }

    @Override
    public Player register(String name, String password) {
        Player player = remoteDataSource.register(name, password);
        if (player.getId() != 0) {
            playerDAO.save(player);
        }
        return player;
    }

    @Override
    public Player changeUserName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Player changePassword(String password) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Player> getFriends() {
        Player currentPlayer = playerDAO.get();
        if (currentPlayer != null) {
            return remoteDataSource.getFriends(currentPlayer.getId());
        }
        return new ArrayList<>();
    }

    @Override
    public Player getCurrentPlayer() {
        return playerDAO.get();
    }

    @Override
    public void logout() {
        playerDAO.clear();
    }
}

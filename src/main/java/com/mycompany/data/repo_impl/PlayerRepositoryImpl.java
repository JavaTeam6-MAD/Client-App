package com.mycompany.data.repo_impl;

import com.mycompany.data.datasource.local.PlayerDAO;
import com.mycompany.data.datasource.remote.RemoteDataSource;
import com.mycompany.data.repo_interface.PlayerRepository;
import com.mycompany.model.app.Player;
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
        Player currentPlayer = playerDAO.get();
        if (currentPlayer == null || currentPlayer.getId() == 0) {
            // Handle case where no user is logged in if necessary, though UI should prevent
            // this
            return new Player();
        }

        Player updatedPlayer = remoteDataSource.changeUserName(currentPlayer.getId(), name);

        if (updatedPlayer != null && updatedPlayer.getId() != 0) {
            // Update local storage
            playerDAO.save(updatedPlayer);
        }
        return updatedPlayer;
    }

    @Override
    public Player changePassword(String password) {
        Player currentPlayer = playerDAO.get();
        if (currentPlayer == null || currentPlayer.getId() == 0) {
            return new Player();
        }

        Player updatedPlayer = remoteDataSource.changePassword(currentPlayer.getId(), password);

        if (updatedPlayer != null && updatedPlayer.getId() != 0) {
            playerDAO.save(updatedPlayer);
        }
        return updatedPlayer;
    }

    @Override
    public Player changeAvatar(String avatar) {
        Player currentPlayer = playerDAO.get();
        if (currentPlayer == null || currentPlayer.getId() == 0) {
            return new Player();
        }

        Player updatedPlayer = remoteDataSource.changeAvatar(currentPlayer.getId(), avatar);

        if (updatedPlayer != null && updatedPlayer.getId() != 0) {
            playerDAO.save(updatedPlayer);
        }
        return updatedPlayer;
    }

    @Override
    public List<Player> getFriends() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Player getCurrentPlayer() {
        return playerDAO.get();
    }

    @Override
    public void logout() {
        Player player = playerDAO.get();
        if (player != null) {
            remoteDataSource.logout(player.getId());
        }
        playerDAO.clear();
    }

    @Override
    public void setPlayerUnavailable() {
        Player player = playerDAO.get();
        if (player != null) {
            remoteDataSource.logout(player.getId());
        }
    }
}

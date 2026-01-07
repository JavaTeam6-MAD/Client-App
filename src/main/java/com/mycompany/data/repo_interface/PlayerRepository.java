/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.data.repo_interface;

import com.mycompany.model.app.Player;
import java.util.List;

/**
 *
 * @author abdel
 */
public interface PlayerRepository {

    Player login(String name, String password);

    Player register(String name, String password);

    Player changeUserName(String name);

    Player changePassword(String password);

    List<Player> getFriends();

    Player getCurrentPlayer();

    void logout();
}

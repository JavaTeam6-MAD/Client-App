/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.data.repo_interface;

import com.mycompany.model.app.Game;
import com.mycompany.model.app.RecordedGame;
import java.util.List;

/**
 *
 * @author abdel
 */
public interface GameRepository {
    List<Game> getGameHistory();
    RecordedGame getRecordedGame(int gameId);
}

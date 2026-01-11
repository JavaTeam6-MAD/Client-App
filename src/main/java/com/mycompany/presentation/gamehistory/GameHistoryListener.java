/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.presentation.gamehistory;

import com.mycompany.model.app.Game;
import java.util.List;

/**
 *
 * @author abdel
 */
public interface GameHistoryListener {
    void onDataLoaded(List<Game> games);
    void onError(String message);
}

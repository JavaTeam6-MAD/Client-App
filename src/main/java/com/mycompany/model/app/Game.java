/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model.app;

import com.mycompany.model.utils.GameStatus;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author abdel
 */
public class Game implements Serializable {
    private static final long serialVersionUID = 1L;
    int id;
    long score;
    Player player1;
    Player player2;
    GameStatus status;
    Date date;
    boolean isRecorded;

    public boolean isIsRecorded() {
        return isRecorded;
    }

    public void setIsRecorded(boolean isRecorded) {
        this.isRecorded = isRecorded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
}

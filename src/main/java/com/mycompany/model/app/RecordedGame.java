/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model.app;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author abdel
 */
public class RecordedGame implements Serializable {
    private static final long serialVersionUID = 1L;

    private int gameId;
    private Date date;
    private int status; // 1: player1 won, 2: player2 won, 3: draw
    private int player1Id;
    private String player1Name;
    private String player1Avatar;
    private int player2Id;
    private String player2Name;
    private String player2Avatar;

    public RecordedGame() {
    }

    public RecordedGame(int gameId, Date date, int status, int player1Id, String player1Name,
            String player1Avatar, int player2Id, String player2Name, String player2Avatar) {
        this.gameId = gameId;
        this.date = date;
        this.status = status;
        this.player1Id = player1Id;
        this.player1Name = player1Name;
        this.player1Avatar = player1Avatar;
        this.player2Id = player2Id;
        this.player2Name = player2Name;
        this.player2Avatar = player2Avatar;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer1Avatar() {
        return player1Avatar;
    }

    public void setPlayer1Avatar(String player1Avatar) {
        this.player1Avatar = player1Avatar;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(int player2Id) {
        this.player2Id = player2Id;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public String getPlayer2Avatar() {
        return player2Avatar;
    }

    public void setPlayer2Avatar(String player2Avatar) {
        this.player2Avatar = player2Avatar;
    }
}

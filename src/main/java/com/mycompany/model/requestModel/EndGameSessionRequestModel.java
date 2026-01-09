/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model.requestModel;

import com.mycompany.model.utils.GameStatus;

/**
 *
 * @author abdel
 */
public class EndGameSessionRequestModel {
    int senderPlayer1Id;
    int receiverPlayer2Id;
    GameStatus player1Status;

    public EndGameSessionRequestModel(int senderPlayer1Id, int receiverPlayer2Id, GameStatus player1Status) {
        this.senderPlayer1Id = senderPlayer1Id;
        this.receiverPlayer2Id = receiverPlayer2Id;
        this.player1Status = player1Status;
    }

    public int getSenderPlayer1Id() {
        return senderPlayer1Id;
    }

    public void setSenderPlayer1Id(int senderPlayer1Id) {
        this.senderPlayer1Id = senderPlayer1Id;
    }

    public int getReceiverPlayer2Id() {
        return receiverPlayer2Id;
    }

    public void setReceiverPlayer2Id(int receiverPlayer2Id) {
        this.receiverPlayer2Id = receiverPlayer2Id;
    }

    public GameStatus getPlayer1Status() {
        return player1Status;
    }

    public void setPlayer1Status(GameStatus player1Status) {
        this.player1Status = player1Status;
    }

   
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model.requestModel;

import java.io.Serializable;

/**
 *
 * @author abdel
 */
public class RejectChallengeRequestModel implements Serializable{
    private static final long serialVersionUID = 1L;
   
    int player1Id;
    int player2Id;

    public RejectChallengeRequestModel(int player1Id, int player2Id) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(int player2Id) {
        this.player2Id = player2Id;
    }
    
}

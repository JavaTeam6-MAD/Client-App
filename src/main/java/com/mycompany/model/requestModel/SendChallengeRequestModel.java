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
public class SendChallengeRequestModel implements Serializable{
    private static final long serialVersionUID = 1L;
   
    int player1Id;
    int player2Id;
    boolean player1isRecording; 

    public SendChallengeRequestModel(int player1Id, int player2Id, boolean player1isRecording) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.player1isRecording = player1isRecording;
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

    public boolean isPlayer1isRecording() {
        return player1isRecording;
    }

    public void setPlayer1isRecording(boolean player1isRecording) {
        this.player1isRecording = player1isRecording;
    }
    
    
    
    
}

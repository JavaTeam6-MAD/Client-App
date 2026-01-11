package com.mycompany.model.requestModel;

import java.io.Serializable;

public class ReceiveChallengeRequestModel implements Serializable {
    private static final long serialVersionUID = 1L;
    int player1Id; // Sender
    int player2Id; // Receiver
    String senderName; // Added for convenience

    public ReceiveChallengeRequestModel(int player1Id, int player2Id, String senderName) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.senderName = senderName;
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
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}

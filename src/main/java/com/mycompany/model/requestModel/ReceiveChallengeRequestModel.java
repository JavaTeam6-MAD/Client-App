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
public class ReceiveChallengeRequestModel implements Serializable {
    private static final long serialVersionUID = 1L;
    int senderPlayerId;
    int receiverPlayer2Id;

    public ReceiveChallengeRequestModel(int senderPlayerId, int receiverPlayer2Id) {
        this.senderPlayerId = senderPlayerId;
        this.receiverPlayer2Id = receiverPlayer2Id;
    }

    public int getSenderPlayerId() {
        return senderPlayerId;
    }

    public void setSenderPlayerId(int senderPlayerId) {
        this.senderPlayerId = senderPlayerId;
    }

    public int getReceiverPlayer2Id() {
        return receiverPlayer2Id;
    }

    public void setReceiverPlayer2Id(int receiverPlayer2Id) {
        this.receiverPlayer2Id = receiverPlayer2Id;
    }
    

   
    
}

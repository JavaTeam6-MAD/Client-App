/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model.responseModel;

import com.mycompany.model.app.Player;
import java.io.Serializable;

/**
 *
 * @author abdel
 */
public class ReceiveChallengeResponseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    int senderPlayerId;
    Player receiverPlayer;
    boolean accepted;
    boolean receiverPlayerIsRecording;

    public ReceiveChallengeResponseModel(int senderPlayerId, Player receiverPlayer, boolean accepted, boolean receiverPlayerIsRecording) {
        this.senderPlayerId = senderPlayerId;
        this.receiverPlayer = receiverPlayer;
        this.accepted = accepted;
        this.receiverPlayerIsRecording = receiverPlayerIsRecording;
    }

    public int getSenderPlayerId() {
        return senderPlayerId;
    }

    public void setSenderPlayerId(int senderPlayerId) {
        this.senderPlayerId = senderPlayerId;
    }

    public Player getReceiverPlayer() {
        return receiverPlayer;
    }

    public void setReceiverPlayer(Player receiverPlayer) {
        this.receiverPlayer = receiverPlayer;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isReceiverPlayerIsRecording() {
        return receiverPlayerIsRecording;
    }

    public void setReceiverPlayerIsRecording(boolean receiverPlayerIsRecording) {
        this.receiverPlayerIsRecording = receiverPlayerIsRecording;
    }
    
}

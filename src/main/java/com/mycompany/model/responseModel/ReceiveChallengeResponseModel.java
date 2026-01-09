package com.mycompany.model.responseModel;

import java.io.Serializable;

public class ReceiveChallengeResponseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    int senderPlayerId;
    int receiverPlayerId;
    boolean accepted;
    boolean receiverPlayerIsRecording;
    // Fields for Game Start details
    int gameId;
    String gameIdUuid; 

    public ReceiveChallengeResponseModel(int senderPlayerId, int receiverPlayerId, boolean accepted, boolean receiverPlayerIsRecording) {
        this.senderPlayerId = senderPlayerId;
        this.receiverPlayerId = receiverPlayerId;
        this.accepted = accepted;
        this.receiverPlayerIsRecording = receiverPlayerIsRecording;
    }
    
    // Overloaded for Game Start
    public ReceiveChallengeResponseModel(int senderPlayerId, int receiverPlayerId, boolean accepted, boolean receiverPlayerIsRecording, String gameIdUuid) {
        this.senderPlayerId = senderPlayerId;
        this.receiverPlayerId = receiverPlayerId;
        this.accepted = accepted;
        this.receiverPlayerIsRecording = receiverPlayerIsRecording;
        this.gameIdUuid = gameIdUuid;
    }

    public int getSenderPlayerId() {
        return senderPlayerId;
    }

    public void setSenderPlayerId(int senderPlayerId) {
        this.senderPlayerId = senderPlayerId;
    }

    public int getReceiverPlayerId() {
        return receiverPlayerId;
    }

    public void setReceiverPlayerId(int receiverPlayerId) {
        this.receiverPlayerId = receiverPlayerId;
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
    
    public String getGameIdUuid() {
        return gameIdUuid;
    }
    
    public void setGameIdUuid(String gameIdUuid) {
        this.gameIdUuid = gameIdUuid;
    }
}
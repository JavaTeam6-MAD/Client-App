/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model.responseModel;

/**
 *
 * @author abdel
 */
public class ReceiveChallengeResponseModel {
    int senderPlayerId;
    int receiverPlayerId;
    boolean accepted;
    boolean receiverPlayerIsRecording;

    public ReceiveChallengeResponseModel(int senderPlayerId, int receiverPlayerId, boolean accepted, boolean receiverPlayerIsRecording) {
        this.senderPlayerId = senderPlayerId;
        this.receiverPlayerId = receiverPlayerId;
        this.accepted = accepted;
        this.receiverPlayerIsRecording = receiverPlayerIsRecording;
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
    
}

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
public class MakeMoveRequestModel implements Serializable {
    private static final long serialVersionUID = 1L;
    int symbol;
    int senderPlayer1Id;
    int receiverPlayer2Id;
    int row;
    int column;

    public MakeMoveRequestModel(int symbol, int senderPlayer1Id, int receiverPlayer2Id, int row, int column) {
        this.symbol = symbol;
        this.senderPlayer1Id = senderPlayer1Id;
        this.receiverPlayer2Id = receiverPlayer2Id;
        this.row = row;
        this.column = column;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
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

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

  
}



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
    int player1Id;
    int player2Id;
    int row;
    int column;

    public MakeMoveRequestModel(int symbol, int player1Id, int player2Id, int row, int column) {
        this.symbol = symbol;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.row = row;
        this.column = column;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
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



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

// returns a list of game - if empty (one game with id 0)
public class getGameHistoryRequestModel implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public getGameHistoryRequestModel(int id) {
        this.id = id;
    }
    
    
}

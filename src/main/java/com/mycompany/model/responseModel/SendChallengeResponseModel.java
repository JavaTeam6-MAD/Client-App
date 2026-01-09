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
public class SendChallengeResponseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    
    boolean accepted;
    Player RequestedPlayer;

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
    
    
}

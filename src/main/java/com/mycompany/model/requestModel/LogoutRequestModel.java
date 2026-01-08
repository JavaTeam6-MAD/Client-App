package com.mycompany.model.requestModel;

import java.io.Serializable;

public class LogoutRequestModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private int playerId;

    public LogoutRequestModel(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}

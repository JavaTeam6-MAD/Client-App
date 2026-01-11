package com.mycompany.model.requestModel;

import java.io.Serializable;

public class GetGamesRequestModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private int playerId;

    public GetGamesRequestModel(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }
}

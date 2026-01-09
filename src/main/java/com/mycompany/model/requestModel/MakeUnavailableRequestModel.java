package com.mycompany.model.requestModel;

import java.io.Serializable;

/**
 *
 * @author abdel
 */
public class MakeUnavailableRequestModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private int playerId;

    public MakeUnavailableRequestModel(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
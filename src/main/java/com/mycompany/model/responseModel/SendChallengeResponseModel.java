package com.mycompany.model.responseModel;

import com.mycompany.model.app.Player;

import java.io.Serializable;

public class SendChallengeResponseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    boolean accepted;
    Player requestedPlayer;
    
    int challengerId; 

    public SendChallengeResponseModel(boolean accepted, int challengerId) {
        this.accepted = accepted;
        this.challengerId = challengerId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public int getChallengerId() {
        return challengerId;
    }

    public void setChallengerId(int challengerId) {
        this.challengerId = challengerId;
    }
}
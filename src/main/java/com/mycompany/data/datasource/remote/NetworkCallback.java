package com.mycompany.data.datasource.remote;

import com.mycompany.model.app.Player;
import com.mycompany.model.requestModel.ReceiveChallengeRequestModel;
import com.mycompany.model.responseModel.ReceiveChallengeResponseModel;
import com.mycompany.model.responseModel.MakeMoveResponseModel;
import com.mycompany.model.requestModel.EndGameSessionRequestModel;

import java.util.List;

public interface NetworkCallback {
    void onFriendsListReceived(List<Player> friends);

    void onChallengeReceived(ReceiveChallengeRequestModel challenge);

    void onChallengeResponse(ReceiveChallengeResponseModel response);

    void onMoveReceived(MakeMoveResponseModel move);

    void onGameEnd(EndGameSessionRequestModel endRequest);

    void onFailure(String errorMessage);
}

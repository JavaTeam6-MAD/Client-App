package com.mycompany.data.datasource.remote;

import com.mycompany.model.app.Player;
import com.mycompany.model.requestModel.ReceiveChallengeRequestModel;
import com.mycompany.model.requestModel.EndGameSessionRequestModel;
import com.mycompany.model.responseModel.MakeMoveResponseModel;
import com.mycompany.model.responseModel.ReceiveChallengeResponseModel;

import java.io.ObjectInputStream;
import java.util.List;

public class ServerListener extends Thread {
    private ObjectInputStream in;
    private NetworkCallback callback;
    private boolean running = true;

    public ServerListener(ObjectInputStream in, NetworkCallback callback) {
        this.in = in;
        this.callback = callback;
    }

    public void setCallback(NetworkCallback callback) {
        this.callback = callback;
    }

    public void stopListener() {
        running = false;
        try {
            // Closing the stream/socket usually interrupts the read,
            // but we might want to interact with RemoteServerConnection instead.
            // For now, we rely on the loop check or IO exception.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (running && !isInterrupted()) {
                Object msg = in.readObject();
                handleMessage(msg);
            }
        } catch (Exception e) {
            System.out.println("ServerListener stopped or connection lost: " + e.getMessage());
            if (running) {
                callback.onFailure("Connection lost");
            }
        }
    }

    private void handleMessage(Object msg) {
        // Run callbacks on JavaFX Inteface Thread?
        // Best practice: The Controller should handle Platform.runLater if needed,
        // BUT to be safe and convenient, we can wrap here or let the caller decide.
        // Let's pass raw data and let Controller use Platform.runLater.

        if (msg instanceof List) {
            try {
                // Assuming it's the friends list
                List<Player> friends = (List<Player>) msg;
                callback.onFriendsListReceived(friends);
            } catch (ClassCastException e) {
                // Ignore if not List<Player>
            }
        } else if (msg instanceof ReceiveChallengeRequestModel) {
            callback.onChallengeReceived((ReceiveChallengeRequestModel) msg);
        } else if (msg instanceof ReceiveChallengeResponseModel) {
            callback.onChallengeResponse((ReceiveChallengeResponseModel) msg);
        } else if (msg instanceof MakeMoveResponseModel) {
            callback.onMoveReceived((MakeMoveResponseModel) msg);
        } else if (msg instanceof EndGameSessionRequestModel) {
            callback.onGameEnd((EndGameSessionRequestModel) msg);
        } else {
            System.out.println("Unknown message received: " + msg.getClass().getSimpleName());
        }
    }
}

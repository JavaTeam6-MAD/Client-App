package com.mycompany.model.notification;

import java.io.Serializable;

/**
 * Notification sent to all clients when the server is shutting down
 */
public class ServerShutdownNotification implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String message;

    public ServerShutdownNotification() {
        this.message = "Server is shutting down";
    }

    public ServerShutdownNotification(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ServerShutdownNotification{" +
                "message='" + message + '\'' +
                '}';
    }
}

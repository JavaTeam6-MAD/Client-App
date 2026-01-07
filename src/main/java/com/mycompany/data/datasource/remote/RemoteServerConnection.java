package com.mycompany.data.datasource.remote;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RemoteServerConnection {
    private static RemoteServerConnection instance;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private RemoteServerConnection() {}

  
    public static RemoteServerConnection getInstance() {
        if (instance == null) instance = new RemoteServerConnection();
        return instance;
    }

    public void connect(String ip, int port) throws Exception {
        this.socket = new Socket(ip, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public void send(Object data) throws Exception {
        if (out != null) {
            out.writeObject(data);
            out.flush();
        }
    }

    public Object receive() throws Exception {
        return (in != null) ? in.readObject() : null;
    }
}

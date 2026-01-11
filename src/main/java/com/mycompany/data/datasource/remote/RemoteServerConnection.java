package com.mycompany.data.datasource.remote;

import com.mycompany.model.app.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class RemoteServerConnection {
    private static RemoteServerConnection instance;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private RemoteServerConnection() {
    }

    public static RemoteServerConnection getInstance() {
        if (instance == null)
            instance = new RemoteServerConnection();
        return instance;
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public void connect(String ip, int port) throws Exception {
        if (socket != null && !socket.isClosed() && socket.isConnected()) {
            return;
        }
        this.socket = new Socket(ip, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public synchronized void disconnect() {
        try {
             
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null && !socket.isClosed())
                socket.close();
            System.out.println("Socket closed");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in = null;
            out = null;
            socket = null;
        }
    }

    public synchronized void send(Object data) throws Exception {
        if (out != null) {
            out.writeObject(data);
            System.out.print(data);
            out.flush();
        }
    }

    public synchronized Object receive() throws Exception {
        return (in != null) ? in.readObject() : null;
    }

    public ObjectInputStream getInputStream() {
        return in;
    }

    public List receiveList() throws IOException, ClassNotFoundException {
        Object response = (in != null) ? in.readObject() : null;
        if (response instanceof List) {
            return (List) response;
        }
        return null;
    }
}

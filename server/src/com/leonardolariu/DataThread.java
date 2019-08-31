package com.leonardolariu;

import java.io.IOException;
import java.net.ServerSocket;

public class DataThread implements Runnable {
    private ServerSocket serverDataSocket;
    private ServerLoad serverLoad;

    public DataThread(ServerSocket serverDataSocket, ServerLoad serverLoad) {
        this.serverDataSocket = serverDataSocket;
        this.serverLoad = serverLoad;
    }

    @Override
    public void run() {
        try {
            while(true) {
                new Thread(new ClientDataThread(serverDataSocket.accept(), serverLoad)).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

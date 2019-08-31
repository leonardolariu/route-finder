package com.leonardolariu;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class ExitThread implements Runnable {
    private ServerSocket serverCommandSocket;
    private ServerSocket serverDataSocket;
    private ServerLoad serverLoad;

    public ExitThread(ServerSocket serverCommandSocket, ServerSocket serverDataSocket, ServerLoad serverLoad) {
        this.serverCommandSocket = serverCommandSocket;
        this.serverDataSocket = serverDataSocket;
        this.serverLoad = serverLoad;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        String command;
        while (!serverLoad.closed) {
            command = scanner.nextLine();

            switch (command) {
                case "exit":
                    try {
                        //stop accepting new clients
                        serverCommandSocket.close();
                        serverDataSocket.close();
                        serverLoad.closed = true;
                        break;
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }

                default:
                    System.out.println("Invalid command!");
                    break;
            }
        }
    }
}

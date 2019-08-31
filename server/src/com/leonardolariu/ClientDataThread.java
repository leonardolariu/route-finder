package com.leonardolariu;

import data.Graph;
import data.StaticGraph;

import java.io.*;
import java.net.Socket;

public class ClientDataThread implements Runnable {
    private Socket socket;
    private ServerLoad serverLoad;

    public ClientDataThread(Socket socket, ServerLoad serverLoad) {
        this.socket = socket;
        this.serverLoad = serverLoad;
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(60000); //1min

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

            String command;

            boolean exited = false;
            while (!exited) {
                command = input.readLine();
                System.out.println("Data Thread: " + command);
                ++serverLoad.inProgress;

                switch (command) {
                    case "get-data":
                        Graph graph = new Graph();
                        graph.setNodes(StaticGraph.getNodes());
                        graph.setEdges(StaticGraph.getEdges());

                        output.writeObject(graph);
                        break;

                    case "exit":
                        exited = true;
                        break;

                    default:
                        System.out.println("Invalid command");
                }

                --serverLoad.inProgress;
                if (serverLoad.closed) exited = true;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("Client disconnected - closed Data Thread");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

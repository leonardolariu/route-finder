package data;

import commands.Command;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class DataSocketThread implements Runnable {
    private Command command;
    private int DATA_PORT;
    private ObjectInputStream input;
    private PrintWriter output;

    private Graph graph;
    private Canvas canvas;
    private int status;

    public DataSocketThread(Command command, int DATA_PORT, Graph graph, Canvas canvas, int status) {
        this.command = command;
        this.DATA_PORT = DATA_PORT;

        this.graph = graph;
        this.canvas = canvas;
        this.status = status;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", DATA_PORT)) {
            socket.setSoTimeout(30000);

            input= new ObjectInputStream(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);

            boolean changed;
            while (command.isRunning()) {
                changed = false;
                output.println("get-data");

                try {
                    Graph temp = (Graph) input.readObject(); //getGraph
                    if (!graph.equals(temp)) {
                        graph.setNodes(temp.getNodes());
                        graph.setEdges(temp.getEdges());

                        changed = true;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                System.out.println("got graph");
                if (changed) {
                    drawGraph(); //drawGraph
                    System.out.println("drawn graph");
                }

                try {
                    Thread.sleep(20000); //wait 20sec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            output.println("exit");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void drawGraph() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.rgb(255, 255, 255, 1));
        g.fillRect(0, 0, 1350, 930);

        HashMap<String, Node> stations = graph.getNodes();
        for (String key : stations.keySet()) {
            if (!stations.get(key).isUsed() && status == 2) continue;

            g.setFill(Color.rgb(255, 0, 0, 1));
            g.fillOval(stations.get(key).getX(), stations.get(key).getY(), 15, 15);

            g.setFill(Color.rgb(0, 0, 0, 1));
            g.fillText(stations.get(key).getName(), stations.get(key).getX() + 15, stations.get(key).getY());
        }

        ArrayList<Edge> edges = graph.getEdges();
        g.setStroke(Color.rgb(0, 0, 0, 1));
        for (Edge e : edges) {
            double x1 = stations.get(e.getNode1()).getX() + 7.5;
            double y1 = stations.get(e.getNode1()).getY() + 7.5;
            double x2 = stations.get(e.getNode2()).getX() + 7.5;
            double y2 = stations.get(e.getNode2()).getY() + 7.5;
            g.strokeLine(x1, y1, x2, y2);

            ArrayList<String> routes = e.getRoutes();
            String routesString = routes.get(0);
            for (int i = 1; i < routes.size(); ++i) {
                routesString += ", " + routes.get(i);
            }
            g.fillText(routesString,(x1 + x2) / 2 + 5, (y1 + y2) / 2 - 5);
        }
    }
}

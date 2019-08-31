package com.leonardolariu;

import data.Edge;
import data.Node;
import data.StaticGraph;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    private static final int COMMAND_PORT = 8100;
    private static final int DATA_PORT = 8101;

    public static void main(String[] args) {
        Connection conn = Database.getConnection();

        System.out.println("Get graph structure...");
        StaticGraph.setNodes(getNodes(conn));
        StaticGraph.setEdges(getEdges(conn));
        Database.releaseConnection();



        ServerLoad serverLoad = new ServerLoad();

        ServerSocket serverCommandSocket = null;
        ServerSocket serverDataSocket = null;

        try {
            serverCommandSocket = new ServerSocket(COMMAND_PORT);
            System.out.println("Listening to COMMAND_PORT " + COMMAND_PORT + "...");

            serverDataSocket = new ServerSocket(DATA_PORT);
            System.out.println("Listening to DATA_PORT " + DATA_PORT + "...");
            new Thread(new DataThread(serverDataSocket, serverLoad)).start();

            new Thread(new ExitThread(serverCommandSocket, serverDataSocket, serverLoad)).start();

            while(true) {
                new Thread(new ClientCommandThread(serverCommandSocket.accept(), serverLoad)).start();
                System.out.println("Client connected");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            //wait all threads to finish their task
            while (serverLoad.inProgress > 0);

            Database.closeConnection();
        }
    }

    public static HashMap<String, Node> getNodes(Connection conn) {
        HashMap<String, Node> stations = new HashMap<>();

        try {
            CallableStatement stmt = conn.prepareCall("select * from admin_nodes");
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
                stations.put(rs.getString(1), new Node(rs.getString(1), rs.getDouble(2), rs.getDouble(3), false));



            stmt = conn.prepareCall("select * from user_nodes");
            rs = stmt.executeQuery();

            while (rs.next())
                stations.get(rs.getString(1)).setUsed(true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return stations;
    }

    public static ArrayList<Edge> getEdges(Connection conn) {
        ArrayList<Edge> edges = new ArrayList<>();

        try {
            CallableStatement stmt = conn.prepareCall("select distinct s1.NAME, s2.NAME from SCHEDULE JOIN STATIONS s1 ON ID_CURR = s1.ID JOIN STATIONS s2 ON ID_NEXT = s2.ID");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                boolean inList = false;
                for (int i = 0; i < edges.size(); ++i)
                    if (edges.get(i).getNode1().equals(rs.getString(2)) && edges.get(i).getNode2().equals(rs.getString(1))) {
                        inList = true;
                        break;
                    }

                if (!inList)
                    edges.add(new Edge(rs.getString(1), rs.getString(2))); //unique edges
            }



            for (Edge e : edges) {
                stmt = conn.prepareCall("select distinct NAME from SCHEDULE JOIN ROUTES ON SCHEDULE.ID_ROUTE = ROUTES.ID AND " +
                        "SCHEDULE.ID_CURR = (select ID FROM STATIONS WHERE NAME = ?) AND SCHEDULE.ID_NEXT = (select ID FROM STATIONS WHERE NAME = ?)");
                stmt.setString(1, e.getNode1());
                stmt.setString(2, e.getNode2());
                rs = stmt.executeQuery();

                while (rs.next())
                    e.addRoute(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return edges;
    }
}

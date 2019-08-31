package com.leonardolariu;

import data.Edge;
import data.Node;
import data.RouteEdge;
import data.StaticGraph;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientCommandThread implements Runnable {
    private Socket socket;
    private ServerLoad serverLoad;

    public ClientCommandThread(Socket socket, ServerLoad serverLoad) {
        this.socket = socket;
        this.serverLoad = serverLoad;
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(300000); //5min

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            String command, username, password;

            String stationName;
            double x, y;

            String routeName;
            int frequency, routeCount;
            String[] route;

            String from, to;
            int startTime;

            Connection conn;

            boolean exited = false;
            while (!exited) {
                command = input.readLine();
                System.out.println("Command Thread: " + command);
                ++serverLoad.inProgress;

                switch (command) {
                    case "login":
                        username = input.readLine();
                        password = input.readLine();

                        conn = Database.getConnection();
                        try (CallableStatement stmt = conn.prepareCall("{?= call manager.check_user(?, ?)}")) {
                            stmt.registerOutParameter(1, Types.INTEGER);
                            stmt.setString(2, username);
                            stmt.setString(3, password);
                            stmt.execute();

                            output.println(stmt.getInt(1));
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }

                        Database.releaseConnection();
                        break;

                    case "register":
                        username = input.readLine();
                        password = input.readLine();

                        conn = Database.getConnection();
                        try (CallableStatement stmt = conn.prepareCall("{?= call manager.add_user(?, 'user', ?)}")) {
                            stmt.registerOutParameter(1, Types.INTEGER);
                            stmt.setString(2, username);
                            stmt.setString(3, password);
                            stmt.execute();

                            output.println(stmt.getInt(1));
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }

                        Database.releaseConnection();
                        break;

                    case "add-station":
                        stationName = input.readLine();
                        x = Double.parseDouble(input.readLine());
                        y = Double.parseDouble(input.readLine());

                        conn = Database.getConnection();
                        try (CallableStatement stmt = conn.prepareCall("{call manager.add_station(?, ?, ?)}")) {
                            stmt.setString(1, stationName);
                            stmt.setDouble(2, x);
                            stmt.setDouble(3, y);
                            stmt.execute();

                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }

                        Database.releaseConnection();
                        break;

                    case "update-station":
                        stationName = input.readLine();
                        x = Double.parseDouble(input.readLine());
                        y = Double.parseDouble(input.readLine());

                        conn = Database.getConnection();
                        try (CallableStatement stmt = conn.prepareCall("update STATIONS set X = ?, Y = ? where NAME = ?")) {
                            stmt.setDouble(1, x);
                            stmt.setDouble(2, y);
                            stmt.setString(3, stationName);
                            stmt.execute();

                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }

                        Database.releaseConnection();
                        break;

                    case "add-route":
                        routeName = input.readLine();
                        frequency = Integer.parseInt(input.readLine());
                        routeCount = Integer.parseInt(input.readLine());
                        route = new String[routeCount];
                        for (int i = 0; i < routeCount; ++i)
                            route[i] = input.readLine();

                        conn = Database.getConnection();
                        add_route(conn, routeName, frequency, route);

                        System.out.println("Get graph structure...");
                        StaticGraph.setNodes(getNodes(conn));
                        StaticGraph.setEdges(getEdges(conn));

                        Database.releaseConnection();
                        break;

                    case "delete-route":
                        routeName = input.readLine();

                        conn = Database.getConnection();
                        try (CallableStatement stmt = conn.prepareCall("delete from ROUTES where NAME like ?")) {
                            stmt.setString(1, routeName);
                            stmt.execute();

                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }

                        System.out.println("Get graph structure...");
                        StaticGraph.setNodes(getNodes(conn));
                        StaticGraph.setEdges(getEdges(conn));

                        Database.releaseConnection();
                        break;

                    case "find-route":
                        from = input.readLine();
                        to = input.readLine();
                        startTime = Integer.parseInt(input.readLine());

                        conn = Database.getConnection();
                        ArrayList<RouteEdge> routeEdges = shortestPath(conn, from, to, startTime);
                        Database.releaseConnection();

                        output.println(routeEdges.size() - 1);
                        for (int i = 1;  i < routeEdges.size(); ++i) {
                            output.println(routeEdges.get(i).parent);
                            output.println(routeEdges.get(i).name);
                        }

                        ArrayList<String> commands = getCommands(routeEdges);
                        output.println(commands.size());
                        for (String comm : commands)
                            output.println(comm);

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
                System.out.println("Client disconnected - closed Command Thread");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }



    private int add_route(Connection conn, String name, int frequency, String[] nodes) {
        try {
            CallableStatement stmt = conn.prepareCall("begin ? := add_route(?,?,?); end;");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, name);
            stmt.setInt(3, frequency);

            ArrayDescriptor arrdesc = ArrayDescriptor.createDescriptor("STRINGLIST", conn);
            Array nodearr = new ARRAY(arrdesc, conn, nodes);
            stmt.setArray(4, nodearr);
            stmt.execute();

            int retval = stmt.getInt(1);
            return retval;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println("add_route error");
            return -1;
        }
    }



    public HashMap<String, Node> getNodes(Connection conn) {
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

    public ArrayList<Edge> getEdges(Connection conn) {
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



    public ArrayList<RouteEdge> shortestPath(Connection conn, String s1, String s2, int timp){
        try {
            CallableStatement stmt = conn.prepareCall("begin ?:= shortestPath(?, ?, ?); end;");
            stmt.registerOutParameter(1, Types.VARCHAR);
            stmt.setString(2, s1);
            stmt.setString(3, s2);
            stmt.setInt(4, timp);

            stmt.execute();
            ArrayList<RouteEdge> retval = new ArrayList<>();
            String res_json = stmt.getNString(1);

            JSONArray arr = new JSONArray(res_json);
            for (int i =0; i < arr.length(); ++i) {
                JSONObject obj = arr.getJSONObject(i);
                String name = obj.getString("Name");
                String parent = obj.getString("Parent");
                int arriv = obj.getInt("Arrival");
                int distance = obj.getInt("Distance");
                String route = obj.getString("Route");
                retval.add(new RouteEdge(name, parent, arriv, distance, route));
            }



            ArrayList<RouteEdge> sorted = new ArrayList<>();
            for (RouteEdge re : retval)
                if (re.parent.equals("ROOT")) {
                    sorted.add(re);
                    break;
                }

            int index = 0;
            while (index < retval.size()-1) {
                for (RouteEdge re : retval) {
                    if (re.parent.equals(sorted.get(index).name)) {
                        sorted.add(re);
                        ++index;
                    }
                }
            }

            for (RouteEdge obj : sorted)
                System.out.println(obj);
            return sorted;
        } catch (Exception e) {
            System.out.println("Shortest path error");
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    public ArrayList<String> getCommands(ArrayList<RouteEdge> routeEdges) {
        ArrayList<String> commands = new ArrayList<>();

        if (routeEdges.get(1).arrival - routeEdges.get(0).arrival > routeEdges.get(1).distance)
            commands.add("Wait " + (routeEdges.get(1).arrival - routeEdges.get(1).distance - routeEdges.get(0).arrival) + " mins (" +
                    (routeEdges.get(0).arrival / 60) + ":" + (routeEdges.get(0).arrival % 60) + " - " +
                    ((routeEdges.get(1).arrival - routeEdges.get(1).distance) / 60) + ":" + ((routeEdges.get(1).arrival - routeEdges.get(1).distance) % 60) + ")");

        routeEdges.get(0).route = routeEdges.get(1).route;
        int startTime = routeEdges.get(1).arrival - routeEdges.get(1).distance;
        int currStation;
        for (currStation = 2; currStation < routeEdges.size(); ++currStation) {
            if (routeEdges.get(currStation).arrival - routeEdges.get(currStation - 1).arrival > routeEdges.get(currStation).distance) {
                commands.add("Get in route " + routeEdges.get(currStation - 1).route + " and get off at station " + routeEdges.get(currStation - 1).name + " (" +
                        (startTime / 60) + ":" + (startTime % 60) + " - " +
                        (routeEdges.get(currStation - 1).arrival / 60) + ":" + (routeEdges.get(currStation - 1).arrival % 60) + ")");

                startTime = routeEdges.get(currStation-1).arrival;
                commands.add("Wait " + (routeEdges.get(currStation).arrival - routeEdges.get(currStation - 1).arrival - routeEdges.get(currStation).distance) + " mins (" +
                        (startTime / 60) + ":" + (startTime %60) + " - " +
                        ((routeEdges.get(currStation).arrival - routeEdges.get(currStation).distance) / 60) + ":" +
                        ((routeEdges.get(currStation).arrival - routeEdges.get(currStation).distance) % 60) + ")");

                startTime = routeEdges.get(currStation).arrival - routeEdges.get(currStation).distance;
            } else if (!routeEdges.get(currStation).route.equals(routeEdges.get(currStation - 1).route)) {
                commands.add("Get in route " + routeEdges.get(currStation - 1).route + " and get off at station " + routeEdges.get(currStation - 1).name + " (" +
                        (startTime / 60) + ":" + (startTime % 60) + " - " +
                        (routeEdges.get(currStation - 1).arrival / 60) + ":" + (routeEdges.get(currStation - 1).arrival % 60) + ")");

                startTime = routeEdges.get(currStation-1).arrival;
            }
        }

        commands.add("Get in route " + routeEdges.get(routeEdges.size() - 1).route + " and get off at station " + routeEdges.get(routeEdges.size() - 1).name + " (" +
                (startTime / 60) + ":" + (startTime % 60) + " - " +
                (routeEdges.get(routeEdges.size() - 1).arrival / 60) + ":" + (routeEdges.get(routeEdges.size() - 1).arrival % 60) + ")");

        return commands;
    }
}

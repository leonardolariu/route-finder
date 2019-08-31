package data;

import java.util.ArrayList;
import java.util.HashMap;

public class StaticGraph {

    private static HashMap<String, Node> stations = new HashMap<>();
    private static ArrayList<Edge> edges;

    private StaticGraph() {
    }



    public static synchronized HashMap<String, Node> getNodes() {
        return stations;
    }

    public static synchronized ArrayList<Edge> getEdges() {
        return edges;
    }

    public static synchronized void setNodes(HashMap<String, Node> nodesParam) {
        stations = nodesParam;
    }

    public static synchronized void setEdges(ArrayList<Edge> edgesParam) {
        edges = edgesParam;
    }



    public static synchronized void addNode(Node n) {
        stations.put(n.getName(), n);
    }

    public static synchronized void updateNode(String stationName, double x, double y) {
        stations.get(stationName).setX(x);
        stations.get(stationName).setY(y);
    }

    public static synchronized boolean hasRoute(String route) {
        for (Edge e : edges)
            if (e.hasRoute(route)) return true;

        return false;
    }

    public static synchronized void updateRoute(String station1, String station2, String routeName) {
        int added = 0;

        for (Edge e : edges) {
            if (e.getNode1().equals(station1) && e.getNode2().equals(station2)) {
                e.addRoute(routeName);
                ++added;
            }
            else if (e.getNode1().equals(station2) && e.getNode2().equals(station1)) {
                e.addRoute(routeName);
                ++added;
            }
        }

        if (added == 0) {
            Edge e = new Edge(station1, station2);
            e.addRoute(routeName);

            edges.add(e);
        }
    }
}

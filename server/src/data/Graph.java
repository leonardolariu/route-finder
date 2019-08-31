package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Graph implements Serializable {
    private static final long serialVersionUID = 1L;

    private HashMap<String, Node> stations = new HashMap<>();
    private ArrayList<Edge> edges;

    public Graph() {
    }



    public synchronized HashMap<String, Node> getNodes() {
        return stations;
    }

    public synchronized ArrayList<Edge> getEdges() {
        return edges;
    }

    public synchronized void setNodes(HashMap<String, Node> nodes) {
        this.stations = nodes;
    }

    public synchronized void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }



    public synchronized void addNode(Node n) {
        stations.put(n.getName(), n);
    }

    public synchronized void updateNode(String stationName, double x, double y) {
        stations.get(stationName).setX(x);
        stations.get(stationName).setY(y);
    }

    public synchronized boolean hasRoute(String route) {
        for (Edge e : edges)
            if (e.hasRoute(route)) return true;

        return false;
    }

    public synchronized void updateRoute(String station1, String station2, String routeName) {
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

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

    public synchronized void deleteRoute(String routeName) {
        for (int i = 0; i < edges.size(); ++i) {
            if (edges.get(i).hasRoute(routeName)) {
                if (edges.get(i).getRoutes().size() == 1) edges.remove(edges.get(i));
                else edges.get(i).removeRoute(routeName);
                --i;
            }
        }
    }



    public boolean equals(Graph obj) {
        if (stations.keySet() != obj.stations.keySet()) return false;
        for (String key : stations.keySet()) {
            if (stations.get(key).getX() != obj.stations.get(key).getX()) return false;
            if (stations.get(key).getY() != obj.stations.get(key).getY()) return false;
            if (!stations.get(key).getName().equals(obj.stations.get(key).getName())) return false;
            if (stations.get(key).isUsed() != obj.stations.get(key).isUsed()) return false;
        }

        if (edges.size() != obj.edges.size()) return false;
        for (int i = 0; i < edges.size(); ++i) {
            if (!edges.get(i).getNode1().equals(obj.edges.get(i).getNode1())) return false;
            if (!edges.get(i).getNode2().equals(obj.edges.get(i).getNode2())) return false;

            if (edges.get(i).getRoutes().size() != obj.edges.get(i).getRoutes().size()) return false;
            for (int j = 0; j < edges.get(i).getRoutes().size(); ++j)
                if (!edges.get(i).getRoutes().get(j).equals(obj.edges.get(i).getRoutes().get(j))) return false;
        }

        return true;
    }
}

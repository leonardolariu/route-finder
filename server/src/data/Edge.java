package data;

import java.io.Serializable;
import java.util.ArrayList;

public class Edge implements Serializable {
    private static final long serialVersionUID = 3L;

    private String node1, node2;
    ArrayList<String> routes = new ArrayList<>();

    public Edge(String node1, String node2) {
        this.node1 = node1;
        this.node2 = node2;
    }



    public String getNode1() {
        return node1;
    }

    public String getNode2() {
        return node2;
    }

    public ArrayList<String> getRoutes() {
        return routes;
    }



    public void addRoute(String route) {
        routes.add(route);
    }

    public boolean hasRoute(String route) {
        for (int i = 0; i < routes.size(); ++i)
            if (routes.get(i).equals(route)) return true;

        return false;
    }
}

package data;

public class RouteEdge {
    public String name;
    public String parent;
    public int arrival;
    public int distance;
    public String route;


    public RouteEdge() {
        name = "errname";
        parent = "errparent";
        arrival = 0x3f3f3f3f;
        distance  = 0x3f3f3f3f;
        route = "errroutr";
    }

    @Override
    public String toString() {
        return "RouteEdge{" +
                "name='" + name + '\'' +
                ", parent='" + parent + '\'' +
                ", arrival=" + arrival +
                ", distance=" + distance +
                ", route='" + route + '\'' +
                '}';
    }

    public RouteEdge(String name, String parent, int arrival,int distance, String route) {
        this.name = name;
        this.parent = parent;
        this.arrival = arrival;
        this.distance = distance;
        this.route = route;
    }
}
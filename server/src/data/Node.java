package data;

import java.io.Serializable;

public class Node implements Serializable {
    private static final long serialVersionUID = 2L;

    private String name;
    private double x, y;
    private boolean used;

    public Node(String name, double x, double y, boolean used) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.used = used;
    }



    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}

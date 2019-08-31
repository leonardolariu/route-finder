package commands;

import data.Edge;

import java.util.ArrayList;

public class Command {
    private boolean running = true;
    private String command;
    private boolean commandToSend = false;

    private String username, password;
    private int authenticateResponse = -1;

    private String stationName;
    private double x, y;

    private String routeName;
    private int frequency;
    private ArrayList<String> route = new ArrayList<>();

    private String from, to;
    private int startTime;
    private boolean found;
    private ArrayList<Edge> edgesToDraw = new ArrayList<>();
    private ArrayList<String> commands = new ArrayList<>();



    public Command() {
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public synchronized String getCommand() {
        if (commandToSend) {
            commandToSend = false;
            return command;
        }

        return null;
    }

    public synchronized void setCommand(String command) {
        this.command = command;
        commandToSend = true;
    }



    public synchronized String getUsername() {
        return username;
    }

    public synchronized void setUsername(String username) {
        this.username = username;
    }

    public synchronized String getPassword() {
        return password;
    }

    public synchronized void setPassword(String password) {
        this.password = password;
    }

    public synchronized int getAuthenticateResponse() {
        return authenticateResponse;
    }

    public synchronized void setAuthenticateResponse(int authenticateResponse) {
        this.authenticateResponse = authenticateResponse;
    }



    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
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



    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public ArrayList<String> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<String> route) {
        this.route = route;
    }



    public synchronized String getFrom() {
        return from;
    }

    public synchronized void setFrom(String from) {
        this.from = from;
    }

    public synchronized String getTo() {
        return to;
    }

    public synchronized void setTo(String to) {
        this.to = to;
    }

    public synchronized int getStartTime() {
        return startTime;
    }

    public synchronized void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public synchronized boolean isFound() {
        return found;
    }

    public synchronized void setFound(boolean found) {
        this.found = found;
    }



    public synchronized ArrayList<Edge> getEdgesToDraw() {
        return edgesToDraw;
    }

    public synchronized void setEdgesToDraw(ArrayList<Edge> edgesToDraw) {
        this.edgesToDraw = edgesToDraw;
    }

    public ArrayList<String> getCommands() {
        return commands;
    }

    public synchronized void setCommands(ArrayList<String> commands) {
        this.commands = commands;
    }
}

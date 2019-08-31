package sample;

import commands.Command;
import data.DataSocketThread;
import data.Edge;
import data.Graph;
import data.Node;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    private Command command;
    private static final int DATA_PORT = 8101;
    private Graph graph;
    private int status;

    @FXML
    Canvas canvas;



    @FXML
    TextField stationName;



    @FXML
    TextField routeNameAdd;

    @FXML
    Spinner<Integer> frequency;

    @FXML
    Spinner<Integer> numberOfStations;

    @FXML
    Label errInvalidRouteConfig;

    @FXML
    ListView<TextField> stationList = new ListView<>();

    @FXML
    Button addRouteSubmitBtn;



    @FXML
    TextField routeName;

    @FXML
    Label errInvalidRouteName;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void myInit(Command command) {
        this.command = command;

        graph = new Graph();
        status = 1;
        new Thread(new DataSocketThread(command, DATA_PORT, graph, canvas, status)).start();
    }



    public void addStation(MouseEvent event) {
        String station = stationName.getText();

        if (!station.equals("")) {
            HashMap<String, Node> stations = graph.getNodes();

            if (stations.get(station) == null) { //add-node
                command.setCommand("add-station"); //configure DB
                command.setStationName(station);
                command.setX(event.getX()); command.setY(event.getY());

                //configure local
                Node n = new Node(station, event.getX(), event.getY(), false);
                graph.addNode(n);
                GraphicsContext g = canvas.getGraphicsContext2D();

                g.setFill(Color.rgb(255, 0, 0, 1));
                g.fillOval(n.getX(), n.getY(), 15, 15);

                g.setFill(Color.rgb(0, 0, 0, 1));
                g.fillText(station, n.getX() + 15, n.getY());
            } else if(!stations.get(station).isUsed()) { //update-node
                command.setCommand("update-station"); //configure DB
                command.setStationName(station);
                command.setX(event.getX()); command.setY(event.getY());

                //configure local
                graph.updateNode(station, event.getX(), event.getY());
                drawGraph();
            }

            stationName.setText("");
        }
    }

    public void addRouteConfig() {
        if (routeNameAdd.getText().equals("") || graph.hasRoute(routeNameAdd.getText())) { //check if the route availability
            routeNameAdd.setText("");
            return;
        }

        if (stationList.getStyleClass().contains("hidden") && addRouteSubmitBtn.getStyleClass().contains("hidden")) {
            stationList.getStyleClass().remove("hidden");
            addRouteSubmitBtn.getStyleClass().remove("hidden");
        }

        if (!errInvalidRouteConfig.getStyleClass().contains("hidden"))
            errInvalidRouteConfig.getStyleClass().add("hidden");

        stationList.getItems().clear();

        for (int i = 1; i <= numberOfStations.getValue(); ++i) {
            stationList.getItems().add(new TextField());
        }
    }

    public void addRouteSubmit() {
        HashMap<String, Node> stations = graph.getNodes();
        ArrayList<String> route = new ArrayList<>();

        for (TextField stationName : stationList.getItems())
            route.add(stationName.getText());

        //validate route
        if (stations.get(route.get(0)) == null) {
            if (errInvalidRouteConfig.getStyleClass().contains("hidden"))
                errInvalidRouteConfig.getStyleClass().remove("hidden");
            return;
        }

        for (int i = 1; i < route.size(); ++i)
            if (stations.get(route.get(0)) == null || route.get(i).equals(route.get(i-1))) {
                if (errInvalidRouteConfig.getStyleClass().contains("hidden"))
                    errInvalidRouteConfig.getStyleClass().remove("hidden");
                return;
            }

        //configure DB
        command.setCommand("add-route");
        command.setRouteName(routeNameAdd.getText());
        command.setFrequency(frequency.getValue());
        command.setRoute(route);

        //configure local
        for (String station : route)
            stations.get(station).setUsed(true);

        for (int i = 1; i < route.size(); ++i)
            graph.updateRoute(route.get(i-1), route.get(i), routeNameAdd.getText());

        drawGraph();

        //hide elements
        if (!errInvalidRouteConfig.getStyleClass().contains("hidden"))
            errInvalidRouteConfig.getStyleClass().add("hidden");

        if (!stationList.getStyleClass().contains("hidden"))
            stationList.getStyleClass().add("hidden");

        if (!addRouteSubmitBtn.getStyleClass().contains("hidden"))
            addRouteSubmitBtn.getStyleClass().add("hidden");
    }

    public void deleteRouteSubmit() {
        if (!graph.hasRoute(routeName.getText())) {
            if (errInvalidRouteName.getStyleClass().contains("hidden"))
                errInvalidRouteName.getStyleClass().remove("hidden");

            return;
        }

        //configure DB
        command.setCommand("delete-route");
        command.setRouteName(routeName.getText());

        //configure local
        graph.deleteRoute(routeName.getText());

        drawGraph();

        //hide elements
        if (!errInvalidRouteName.getStyleClass().contains("hidden"))
            errInvalidRouteName.getStyleClass().add("hidden");
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
            g.fillText(routesString,(x1 + x2) / 2 + 15, (y1 + y2) / 2);
        }
    }
}

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
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    private Command command;
    private static final int DATA_PORT = 8101;
    private Graph graph;
    private int status;

    @FXML
    Canvas canvas;



    @FXML
    TextField from;

    @FXML
    TextField to;

    @FXML
    Spinner<Integer> hour;

    @FXML
    Spinner<Integer> mins;

    @FXML
    Label errInvalidEndpoints;



    @FXML
    Button submitBtn;

    @FXML
    ListView<Label> commands = new ListView<>();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void myInit(Command command) {
        this.command = command;

        graph = new Graph();
        status = 2;
        new Thread(new DataSocketThread(command, DATA_PORT, graph, canvas, status)).start();
    }



    public void findRoute() {
        int startTime = hour.getValue()*60 + mins.getValue();
        if (from.getText().equals(command.getFrom()) && to.getText().equals(command.getTo()) && startTime == command.getStartTime()) //no change since last request
            return;
        else {
            GraphicsContext g = canvas.getGraphicsContext2D();

            //validate request
            HashMap<String, Node> stations = graph.getNodes();

            if (stations.get(from.getText()) == null || stations.get(to.getText()) == null || from.getText().equals(to.getText())) { //invalid
                if (errInvalidEndpoints.getStyleClass().contains("hidden"))
                    errInvalidEndpoints.getStyleClass().remove("hidden");

                if (!commands.getStyleClass().contains("hidden"))
                    commands.getStyleClass().add("hidden");

                g.setStroke(Color.rgb(0, 0, 0, 1));
                for (Edge e : command.getEdgesToDraw()) {
                    double x1 = stations.get(e.getNode1()).getX() + 7.5;
                    double y1 = stations.get(e.getNode1()).getY() + 7.5;
                    double x2 = stations.get(e.getNode2()).getX() + 7.5;
                    double y2 = stations.get(e.getNode2()).getY() + 7.5;
                    g.strokeLine(x1, y1, x2, y2);
                }

                return;
            } else { //valid
                if (!errInvalidEndpoints.getStyleClass().contains("hidden"))
                    errInvalidEndpoints.getStyleClass().add("hidden");

                g.setStroke(Color.rgb(0, 0, 0, 1));
                for (Edge e : command.getEdgesToDraw()) {
                    double x1 = stations.get(e.getNode1()).getX() + 7.5;
                    double y1 = stations.get(e.getNode1()).getY() + 7.5;
                    double x2 = stations.get(e.getNode2()).getX() + 7.5;
                    double y2 = stations.get(e.getNode2()).getY() + 7.5;
                    g.strokeLine(x1, y1, x2, y2);
                }

                //configure DB
                command.setCommand("find-route");
                command.setFrom(from.getText());
                command.setTo(to.getText());
                command.setStartTime(startTime);
                command.setFound(false);

                //configure local
                while (!command.isFound()); //wait for server response

                //draw edges
                g.setStroke(Color.rgb(255, 255, 0, 1));
                for (Edge e : command.getEdgesToDraw()) {
                    double x1 = stations.get(e.getNode1()).getX() + 7.5;
                    double y1 = stations.get(e.getNode1()).getY() + 7.5;
                    double x2 = stations.get(e.getNode2()).getX() + 7.5;
                    double y2 = stations.get(e.getNode2()).getY() + 7.5;
                    g.strokeLine(x1, y1, x2, y2);
                }

                //show listView
                commands.getItems().clear();
                if (commands.getStyleClass().contains("hidden"))
                    commands.getStyleClass().remove("hidden");

                //print commands in listView
                for (int i = 0; i < command.getCommands().size(); ++i) {
                    Label comm = new Label();
                    comm.setText(command.getCommands().get(i));

                    commands.getItems().add(comm);
                }
            }
        }
    }
}

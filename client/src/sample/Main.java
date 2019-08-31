package sample;

import commands.Command;
import commands.CommandSocketThread;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static final int COMMAND_PORT = 8100;

    @Override
    public void start(Stage primaryStage) {
        Command command = new Command();
        new Thread(new CommandSocketThread(command, COMMAND_PORT)).start();

        try {
            //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
            Parent root = loader.load();

            Controller controller = loader.getController();
            controller.myInit(command);

            primaryStage.setTitle("RouteFinder");
            primaryStage.setScene(new Scene(root, 1920, 1015));
            primaryStage.show();

            primaryStage.setOnCloseRequest(e -> {
                e.consume();
                command.setCommand("exit");
                primaryStage.close();
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

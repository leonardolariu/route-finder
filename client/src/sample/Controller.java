package sample;

import commands.Command;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private Command command;

    @FXML
    Label errInvalidCredentials;

    @FXML
    TextField username;

    @FXML
    PasswordField password;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void myInit(Command command) {
        this.command = command;
    }



    public void showErrors() {
        errInvalidCredentials.getStyleClass().remove("hidden");
        //errInvalidCredentials.getStyleClass().add("visible");
    }

    public void hideErrors() {
        //errInvalidCredentials.getStyleClass().remove("visible");
        if (!errInvalidCredentials.getStyleClass().contains("hidden"))
        errInvalidCredentials.getStyleClass().add("hidden");
    }

    public void setRegisterScene(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
        Parent root = loader.load();

        RegisterController controller = loader.getController();
        controller.myInit(command);

        Scene newScene = new Scene(root);

        //this line gets the Stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(newScene);
        window.show();
    }

    public void login(MouseEvent event) throws IOException {
        hideErrors();

        if (!(username.getText().equals("")) && !((password.getText()).equals(""))) {
            command.setCommand("login");
            command.setUsername(username.getText());
            command.setPassword(password.getText());

            username.setText(""); password.setText("");

            int response = command.getAuthenticateResponse();
            for (; response == -1; response = command.getAuthenticateResponse());
            command.setAuthenticateResponse(-1);

            if (response == 0) showErrors();
            else if (response == 1) {
                System.out.println("admin login success");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
                Parent root = loader.load();

                AdminController controller = loader.getController();
                controller.myInit(command);

                Scene newScene = new Scene(root);

                //this line gets the Stage info
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

                window.setScene(newScene);
                window.show();
            } else if (response == 2) {
                System.out.println("user login success");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("user.fxml"));
                Parent root = loader.load();

                UserController controller = loader.getController();
                controller.myInit(command);

                Scene newScene = new Scene(root);

                //this line gets the Stage info
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

                window.setScene(newScene);
                window.show();
            }
        } else {
            username.setText(""); password.setText("");
            showErrors();
        }
    }
}

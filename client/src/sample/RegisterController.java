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

public class RegisterController implements Initializable {
    private Command command;

    @FXML
    TextField username;

    @FXML
    Label errUsernameTaken;

    @FXML
    PasswordField password;

    @FXML
    PasswordField confirmPassword;

    @FXML
    Label errPassNoMatch;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void myInit(Command command) {
        this.command = command;
    }



    public void showError(int errType) {
        if (errType == 1) {
            errUsernameTaken.getStyleClass().remove("hidden");
            //errUsernameTaken.getStyleClass().add("visible");
        }
        else {
            errPassNoMatch.getStyleClass().remove("hidden");
            //errPassNoMatch.getStyleClass().add("visible");
        }
    }

    public void hideErrors() {
        //errUsernameTaken.getStyleClass().remove("visible");
        if (!errUsernameTaken.getStyleClass().contains("hidden"))
            errUsernameTaken.getStyleClass().add("hidden");

        //errPassNoMatch.getStyleClass().remove("visible");
        if (!errPassNoMatch.getStyleClass().contains("hidden"))
        errPassNoMatch.getStyleClass().add("hidden");
    }

    public void setLoginScene(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();

        Controller controller = loader.getController();
        controller.myInit(command);

        Scene newScene = new Scene(root);

        //this line gets the Stage info
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(newScene);
        window.show();
    }

    public void register(MouseEvent event) throws IOException {
        hideErrors();

        if (!(password.getText()).equals(confirmPassword.getText())) {
            password.setText(""); confirmPassword.setText("");
            showError(2);
        } else if (!(username.getText().equals("")) && !((password.getText()).equals(""))) {
            command.setCommand("register");
            command.setUsername(username.getText());
            command.setPassword(password.getText());

            username.setText("");

            int response = command.getAuthenticateResponse();
            for (; response == -1; response = command.getAuthenticateResponse());
            command.setAuthenticateResponse(-1);

            if (response == 0) showError(1);
            else {
                System.out.println("register success");

                //change scene
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
        }
    }
}

package hr.gamestop.controller;

import hr.gamestop.Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;


    @FXML
    private void onLogin() {

        String username = usernameField.getText();
        String password = passwordField.getText();


        if (username.equals("luka")
                && password.equals("luka123")) {

            openGameScene();

        } else {

            messageLabel.setText(
                    "Pogrešno korisničko ime ili lozinka."
            );

            messageLabel.setStyle(
                    "-fx-text-fill: red;"
            );
        }
    }


    private void openGameScene() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            Main.class.getResource(
                                    "/hr/gamestop/game-view.fxml"
                            )
                    );


            Scene gameScene =
                    new Scene(
                            loader.load()
                    );


            Stage stage =
                    (Stage)
                            usernameField
                                    .getScene()
                                    .getWindow();


            stage.setScene(gameScene);

            stage.setTitle(
                    "GameStop Manager"
            );


        } catch (IOException e) {

            messageLabel.setText(
                    "Greška pri otvaranju GameStop Managera."
            );

            messageLabel.setStyle(
                    "-fx-text-fill: red;"
            );

            e.printStackTrace();
        }
    }
}
package gui;

import database.Datasource;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.swing.text.PasswordView;

public class LoginWindowController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    /**
     * Queries the database and attempts to log the user in.
     */
    public boolean queryLogin(){
        Datasource datasource = Datasource.getInstance();
        String usernameValue = usernameField.getText();
        String passwordValue = passwordField.getText();
        System.out.println(usernameValue + " " + passwordValue);
        return datasource.queryUserLogin(usernameValue, passwordValue);
    }
}

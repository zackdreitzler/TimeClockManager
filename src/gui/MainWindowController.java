package gui;

import database.Datasource;
import database.Shift;
import database.ShiftData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class MainWindowController {
    private Datasource datasource = Datasource.getInstance();
    ShiftData shiftData = new ShiftData();
    @FXML
    private TableView<Shift> shiftTableview;
    @FXML
    private BorderPane mainWindow;

    /**
     * Handler for the login button. This allows the user
     * to log into their account and pull their shifts.
     */
    @FXML
    private void login() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainWindow.getScene().getWindow());
        dialog.setTitle("Login");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("loginWindow.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        boolean loginSuccess;
        do{
            Optional<ButtonType> result = dialog.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK) {
                LoginWindowController loginWindowController = fxmlLoader.getController();
                loginSuccess = loginWindowController.queryLogin();
            }else{
                break;
            }
        }while(!loginSuccess);

        //Here is where shifts are added to shiftdata and then the tableview is updated.
        updateTable();
    }


    /**
     * Handler for the Time Punch button. This allows the user to
     * clock in and out of the system.
     */
    @FXML
    public void punchClock(){
        if(!datasource.queryEmployeeClockStatus()){
            datasource.clockInEmployeeOnTable();
        }else{
            datasource.clockOutEmployeeOnTable();
        }
        updateTable();
    }

    /**
     * Reads in the shifts and updates the table.
     */
    private void updateTable(){
        shiftData.clear();
        shiftData.readInShifts(datasource);
        shiftTableview.setItems(shiftData.getShifts());
    }
}

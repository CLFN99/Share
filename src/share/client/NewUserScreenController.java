package share.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import share.interfaces.*;
import share.models.*;

import java.rmi.RemoteException;

public class NewUserScreenController {
    private ISession manager;
    @FXML
    private TextField tbEmail;

    @FXML
    private TextField tbUsername;

    @FXML
    private TextField tbPassword;

    @FXML
    private TextArea tbBio;

    @FXML
    void btnSaveUser(ActionEvent event) {
        if(!tbEmail.getText().equals("") && !tbUsername.getText().equals("") && !tbPassword.getText().equals("")){
            User u = new User(tbUsername.getText(), tbPassword.getText(), tbEmail.getText(), tbBio.getText());
            try {
                int id = manager.registerNewUser(u);
                if(id >= 0){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Gebruiker geregistreerd! U kunt nu inloggen.", ButtonType.OK);
                    alert.showAndWait();
                }
                else if(id == -2){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Dit email adres bestaat al. Voer een andere in.", ButtonType.OK);
                    alert.showAndWait();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) tbEmail.getScene().getWindow();
        stage.close();
    }

    public void setManagers(ISession manager) {
        this.manager = manager;
    }
}

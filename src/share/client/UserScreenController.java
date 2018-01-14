package share.client;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import share.interfaces.IMain;
import share.interfaces.ISession;
import share.models.*;

import java.rmi.RemoteException;


public class UserScreenController {

    private ISession sessionManager;
    private IMain manager;
    private User currentUser;
    private User user;

    @FXML
    private Label lblUsername;

    @FXML
    private Text txtBio;

    @FXML
    private Button btnAddFriend;

    @FXML
    void btnAddFriend_Click(Event event) {
        try {
            if(manager.addFriend(currentUser, user)){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Toegevoegd aan vrienden!", ButtonType.OK);
                alert.showAndWait();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Er ging iets mis!", ButtonType.OK);
                alert.showAndWait();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setManagers(IMain manager, ISession sessionManager, User currentUser, User user){
        this.manager = manager;
        this.sessionManager = sessionManager;
        this.currentUser = currentUser;
        this.user = user;
        lblUsername.setText(user.getUsername());
        txtBio.setText(user.getBio());
        for(User friend : currentUser.getFriends()){
            if(friend.getId() == user.getId()){
                btnAddFriend.setDisable(true);
            }
        }
    }
}

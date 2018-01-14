package share.client;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import share.interfaces.IMain;
import share.interfaces.ISession;
import share.models.*;

import java.io.IOException;
import java.rmi.RemoteException;


public class UserScreenController {

    private IMain manager;
    private User currentUser;
    private User user;

    @FXML
    private Label lblUsername;
    @FXML
    private Button btnStartChat;
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

    @FXML
    void btnStartChat_Click(ActionEvent event) {
        Chat c = new Chat(currentUser, user);
        c.initManager(manager);
        int id = c.register();
        if(id != -1){
            c.setId(id);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatScreen.fxml"));
            Parent root1 = null;
            try {
                root1 = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatScreenController controller = fxmlLoader.getController();
            controller.setManagers(manager, c, currentUser);
            Stage stage = new Stage();
            stage.setTitle("Chat");
            stage.setScene(new Scene(root1));
            stage.setOnHidden(e -> {
                controller.shutdown();
                stage.close();
            });
            stage.show();
            Stage stage1 = (Stage) btnStartChat.getScene().getWindow();
            stage1.close();
        }
    }

    public void setManagers(IMain manager, User currentUser, User user){
        this.manager = manager;
        this.currentUser = currentUser;
        this.user = user;
        lblUsername.setText(user.getUsername());
        txtBio.setText(user.getBio());
        for(User friend : currentUser.getFriends()){
            if(friend.getId() == user.getId()){
                btnAddFriend.setDisable(true);
            }
        }

        try {
            for(Chat c : manager.getChats(currentUser.getId())){
                if(c.getUsers().get(0).getId() == user.getId() || c.getUsers().get(1).getId() == user.getId()){
                    btnStartChat.setDisable(true);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

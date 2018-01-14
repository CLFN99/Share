package share.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import share.interfaces.IMain;
import share.interfaces.ISession;
import share.models.User;

import java.rmi.RemoteException;


public class NewPostController {
    private IMain manager;
    private User currentUser;
//todo check if char < max
    @FXML
    private TextArea txtNewPost;

    @FXML
    void btnSaveNewPost_Click(ActionEvent event) {
        try {
            if(manager.newPost(txtNewPost.getText(), currentUser) != null){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Post toegevoegd!!", ButtonType.OK);
                alert.showAndWait();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setManagers(IMain manager, User u) {
        this.manager = manager;
        this.currentUser = u;
    }
}

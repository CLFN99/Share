package share.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import share.interfaces.IMain;
import share.models.Post;
import share.models.*;

import java.rmi.RemoteException;

public class ChangePostController {
    private IMain manager;
    private User currentUser;
    private Post post;
    @FXML
    private TextArea txtNewPost;

    @FXML
    void btnSavePost_Click(ActionEvent event) {
        try {
            manager.updatePost(post, txtNewPost.getText());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Wijziging opgeslagen!", ButtonType.OK);
            alert.showAndWait();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setManagers(IMain manager, User u, Post post) {
        this.manager = manager;
        this.currentUser = u;
        this.post = post;
        txtNewPost.setText(post.getText());
    }
}

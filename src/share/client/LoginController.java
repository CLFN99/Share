package share.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import share.interfaces.IMain;
import share.interfaces.ISession;
import share.models.*;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private ISession sessionManager;
    private IMain manager;
    private User u;

    @FXML
    private Button btnLogIn;

    @FXML
    private TextField tbEmail;

    @FXML
    private TextField tbPwd;

    @FXML
    private Hyperlink linkRegister;

    @FXML
    void btnLogIn_Click(ActionEvent event) {
        String email = tbEmail.getText();
        String password = tbPwd.getText();
        try {
            u = sessionManager.logIn(email, password);
            if( u != null){

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
                    Parent root1 = null;
                    root1 = fxmlLoader.load();
                    MainScreenController controller = fxmlLoader.getController();
                    controller.setManagers(this.manager, this.sessionManager, this.u);
                    Stage stage = new Stage();
                    stage.setTitle("Share");
                    stage.setScene(new Scene(root1));
                    stage.show();

                    stage.setOnHidden(e -> {
                        controller.shutdown();
                        stage.close();
                    });
                    Stage stage1 = (Stage) btnLogIn.getScene().getWindow();
                    stage1.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Er ging iets mis! Voer een juist email-adres en wachtwoord in.", ButtonType.OK);
                alert.showAndWait();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void linkRegister_Click(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewUserScreen.fxml"));
        Parent root1 = null;
        try {
            root1 = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NewUserScreenController controller = fxmlLoader.getController();
        controller.setManagers(this.sessionManager);
        Stage stage = new Stage();
        stage.setTitle("Share");
        stage.setScene(new Scene(root1));
        stage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setManagers(IMain manager, ISession sessionManager){
        this.manager = manager;
        this.sessionManager = sessionManager;
    }
}

package share.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import share.interfaces.IMain;
import share.interfaces.ISession;
import share.models.SessionManager;
import share.models.User;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {

    private ISession sessionManager;
    private IMain manager;
    private User u;


    @FXML
    private TabPane tabPane;
    //private SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();

    @FXML
    private AnchorPane tabFeed;

    @FXML
    private AnchorPane tabChat;

    @FXML
    private AnchorPane tabProfiel;

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblBio;

    @FXML
    private Button btnChangeBio;
    @FXML
    private TextField tbSearch;
    @FXML
    private AnchorPane tabFriends;

    @FXML
    private AnchorPane tabSearchResults;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnLogOut;

    @FXML
    private ListView<Label> resultListView;

    private ObservableList<Label> resultUsers = FXCollections.observableArrayList();
    @FXML
    void btnChangeBio_Click(ActionEvent event) {

    }

    @FXML
    void btnLogOut_Click(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Weet je zeker dat je wilt uitloggen?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                if(sessionManager.logOut(sessionManager.getSession(u))){
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
                        Parent root1 = null;
                        root1 = fxmlLoader.load();
                        LoginController controller = fxmlLoader.getController();
                        controller.setManagers(this.manager, this.sessionManager);
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root1));
                        stage.show();
                        Stage stage1 = (Stage) btnLogOut.getScene().getWindow();
                        stage1.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    void btnSearch_Click(ActionEvent event) {
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        try {
            List<User> results = manager.searchUser(tbSearch.getText());
            tabSearchResults.setDisable(false);
            selectionModel.select(4);
            if(results.isEmpty()){
                Label label = new Label();

                resultUsers.add(label);
                label.setText("er zijn geen gebruikers met deze gebruikersnaam");
            }
            else{
                for(User u : results){
                    Label label = new Label();

                    resultUsers.add(label);
                    label.setText(u.getUsername());
                }
            }


        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openChatTab(Event event) {

    }

    @FXML
    void openFeedTab(Event event) {

    }

    @FXML
    protected void initialize() {
        resultListView.setItems(resultUsers);
    }
    @FXML
    void openFriendsTab(Event event) {

    }

    @FXML
    void openProfileTab(Event event) {
        lblUsername.setText(u.getUsername());
        lblBio.setText(u.getBio());
    }

    @FXML
    void openSearchResults(Event event) {

    }

    public void setManagers(IMain manager, ISession sessionManager, User u){
        this.manager = manager;
        this.sessionManager = sessionManager;
        this.u = u;
        resultListView.setItems(resultUsers);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

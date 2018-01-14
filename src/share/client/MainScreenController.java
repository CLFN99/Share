package share.client;

import javafx.application.Platform;
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
import share.models.*;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {

    private ISession sessionManager;
    private IMain manager;
    private User currentUser;
    private List<User> results;
    private List<Chat> chats;
    @FXML
    private ListView<Label> chatsListView;

    @FXML
    private TabPane tabPane;

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

    @FXML
    private ListView<Label> listViewFriends;

    private ObservableList<Label> resultUsers = FXCollections.observableArrayList();
    private ObservableList<Label> friends = FXCollections.observableArrayList();
    private ObservableList<Label> chatList = FXCollections.observableArrayList();

    @FXML
    void btnChangeBio_Click(ActionEvent event) {

    }

    @FXML
    void btnLogOut_Click(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Weet je zeker dat je wilt uitloggen?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                if(sessionManager.logOut(sessionManager.getSession(currentUser))){
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
            results = manager.searchUser(tbSearch.getText());
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
        tbSearch.setText("");
    }

    @FXML
    void listItem_Click(Event event) {
        User user = null;
        for(User u : results){
            if(u.getUsername() == resultListView.getSelectionModel().getSelectedItem().getText()){
                user = u;
                break;
            }
        }
        if(user != null){
            goToUserProfile(user);
        }

    }

    @FXML
    void openChatTab(Event event) {
        chatList.clear();
        chatsListView.getItems().clear();
        try {
            chats = manager.getChats(currentUser.getId());
            if(!chats.isEmpty()){
                for(Chat c : chats){
                    Label label = new Label();
                    chatList.add(label);
                    label.setText(c.getId() + ": Chat tussen " + c.getUsers().get(0).getUsername() + " en " + c.getUsers().get(1).getUsername());
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
        friends.clear();
        listViewFriends.getItems().clear();
        for(User us : currentUser.getFriends()){
            Label label = new Label();
            friends.add(label);
            label.setText(us.getUsername());
        }
    }

    @FXML
    void openProfileTab(Event event) {
        lblUsername.setText(currentUser.getUsername());
        lblBio.setText(currentUser.getBio());
    }

    @FXML
    void friendClicked(Event event) {

        User user = null;
        for(User friend : currentUser.getFriends()){
            if(friend.getUsername() == listViewFriends.getSelectionModel().getSelectedItem().getText()){
                user = friend;
                break;
            }
        }
        if(user != null){
            goToUserProfile(user);
        }
    }

    @FXML
    void goToChat_Click(Event event){
        Chat c = null;
        String chatString = chatsListView.getSelectionModel().getSelectedItem().getText();
        String required = chatString.substring(0, chatString.indexOf(":"));
        for(Chat chat : chats){
            if(chat.getId() == Integer.parseInt(required)){
                c = chat;
                break;
            }
        }
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
    }

    @FXML
    void openSearchResults(Event event) {

    }

    public void setManagers(IMain manager, ISession sessionManager, User u){
        this.manager = manager;
        this.sessionManager = sessionManager;
        this.currentUser = u;
        resultListView.setItems(resultUsers);
        listViewFriends.setItems(friends);
        chatsListView.setItems(chatList);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    void goToUserProfile(User user){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UserScreen.fxml"));
        Parent root1 = null;
        try {
            root1 = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UserScreenController controller = fxmlLoader.getController();
        controller.setManagers(this.manager, this.sessionManager, this.currentUser, user);
        Stage stage = new Stage();
        stage.setTitle("Gebruiker");
        stage.setScene(new Scene(root1));
        stage.show();
    }

}

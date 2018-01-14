package share.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import share.interfaces.IMain;
import share.interfaces.ISession;
import share.models.Chat;
import share.models.*;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatScreenController {
    private IMain manager;
    private User currentUser;
    private Chat c;
    private ObservableList<Label> messageList = FXCollections.observableArrayList();
    private Timer timer;
    private ChatController chatController;

    @FXML
    private ListView<Label> msgListView;

    @FXML
    private TextField tbMsg;

    @FXML
    private Button btnSendMessage;

    //todo: refresh messages every 300ms
    @FXML
    void btnSendMessage_Click(Event event) {
        try {
            if(manager.sendMessage(currentUser, tbMsg.getText(), c.getId())){
                tbMsg.setText("");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setManagers(IMain manager, Chat c, User currentUser){
        this.manager = manager;
        this.currentUser = currentUser;
        this.c = c;
        try {
            chatController = new ChatController(manager.getPublisher(), this, c);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        msgListView.setItems(messageList);
        updateUI();
    }

    public void updateUI() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        msgListView.getItems().clear();
                        messageList.clear();
                        for (Message msg : c.getMessages()) {
                            Label lbl = new Label();
                            messageList.add(lbl);
                            lbl.setText(msg.getUser().getUsername() + ": " + msg.getText());
                        }
                    }
                });
            }
        }, 0, 50);

    }

    public void shutdown(){
        timer.cancel();
        chatController.unsubscribe();
    }

    public ChatController getChatController(){return chatController;}

    public void setMessages(List<Message> messages){
        c.setMessages(messages);
    }
}



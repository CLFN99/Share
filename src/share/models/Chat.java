package share.models;

import share.client.ChatScreenController;
import share.interfaces.IChat;
import share.interfaces.IMain;
import share.server.IRemotePropertyListener;
import share.server.IRemotePublisher;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Chat implements IChat, Serializable {
    private int id;
    private List<Message> messages;
    private List<User> users;
    private transient IMain manager;
    private transient ChatScreenController controller;
    static final long serialVersionUID = 1945888197085269794L;
    /**
     * instantiates a new Chat
     * @param userA the first user participating in the chat
     * @param userB the second user participating in the chat
     */
    public Chat(User userA, User userB){
        messages = new ArrayList<>();
        users = new ArrayList<>();
        users.add(userA);
        users.add(userB);
        id = -1;
        register();
    }

    /**
     * gets the messages of the chat
     * @return this.messages
     */
    public List<Message> getMessages(){
        return this.messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    /**
     * gets the current chat's id
     * @return this.id
     */
    public int getId(){return id;}

    /**
     * gets the chat's users
     * @return this.users
     */
    public List<User> getUsers(){return this.users;}

    @Override
    public boolean register() {
        if(manager != null){
            try {
                return manager.newChat(this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public void initManager(IMain manager){
        this.manager = manager;
    }




}

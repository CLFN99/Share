package share.models;

import share.interfaces.IChat;
import share.interfaces.IMain;
import share.server.IRemotePropertyListener;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Chat implements IChat, IRemotePropertyListener {
    private int id;
    private List<Message> messages;
    private List<User> users;
    private Manager manager;


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
    public void register() {
        manager.newChat(this);
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public void initManager(IMain manager){
        this.manager = (Manager) manager;
    }

    @Override
    public void propertyChange(PropertyChangeEvent var1) throws RemoteException {
        this.messages.add((Message) var1.getNewValue());
    }
}

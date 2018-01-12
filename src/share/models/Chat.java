package share.models;

import share.interfaces.IChat;
import share.interfaces.IMain;
import share.server.IRemotePropertyListener;
import share.server.IRemotePublisher;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Chat implements IChat, IRemotePropertyListener {
    private int id;
    private List<Message> messages;
    private List<User> users;
    private IMain manager;
    private IRemotePublisher publisher;

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
        try {
            publisher.subscribePropertyListener(this, "chat");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
        try {
            manager.newChat(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public void initManager(IMain manager){
        this.manager = manager;
    }
    public void initPublisher(IRemotePublisher publisher){this.publisher = publisher;}

    @Override
    public void propertyChange(PropertyChangeEvent var1) throws RemoteException {
        this.messages.add((Message) var1.getNewValue());
    }
}

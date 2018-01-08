package models;

import interfaces.IChat;

import java.util.ArrayList;
import java.util.List;

public class Chat implements IChat {
    private String id;
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
    }

    /**
     * gets the messages of the chat
     * @return this.messages
     */
    public List<Message> getMessages(){
        return this.messages;
    }

    /**
     * calls IMain.sendMessage
     * @param text the text the user provided
     */
    public void newMessage(String text){

    }

    /**
     * gets the current chat's id
     * @return this.id
     */
    public String getId(){return id;}

    /**
     * gets the chat's users
     * @return this.users
     */
    public List<User> getUsers(){return this.users;}

    @Override
    public void register() {

    }

    @Override
    public void receiveMessage(Message msg) {

    }

    @Override
    public void setId(String id) {

    }
}

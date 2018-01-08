package interfaces;

import models.Message;

public interface IChat {
    /**
     * this registers the chat in the list of active chats on the main server
     * calls IMain.newChat
     */
    void register();

    /**
     * called by mains server in case of new message
     * @param msg the new message
     */
    void receiveMessage(Message msg);

    /**
     * sets the chat ID
     * called by main server upon creation of the chat
     * @param id a unique ID
     */
    void setId(String id);
}

package share.interfaces;

import share.models.Message;

public interface IChat {
    /**
     * this registers the chat in the list of active chats on the main share.tests.server
     * calls IMain.newChat
     */
    boolean register();

    /**
     * sets the chat ID
     * called by main share.tests.server upon creation of the chat
     * @param id a unique ID
     */
    void setId(int id);
}

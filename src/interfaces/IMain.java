package interfaces;

import models.*;

import java.rmi.Remote;
import java.util.List;

public interface IMain extends Remote {
    /**
     * gets the posts of a feed
     * @param email of the user that owns the feed
     * @return the posts of the feed
     */
    List<Post> refreshFeed(String email);

    /**
     * searches the database for a user
     * @param username the search paramater
     * @return the user if found, otherwise null
     */
    User searchUser(String username);

    /**
     * adds a session to the list of active sessions
     * also sets the session Id
     * @param session to add
     * @return true if succeeded, false if something went wrong
     */
    boolean addSession(Session session);

    /**
     * removes a session from the list of active sessions
     * @param session to remove
     * @return true if succeeded, false if something went wrong
     */
    boolean removeSession(Session session);

    /**
     * crates a new chat and adds it to the list of active chats
     * @param userA first user that participates in the chat
     * @param userB second user that participates in the chat
     * @return true if succeeded, false if something went wrong
     */
    boolean newChat(User userA, User userB);

    /**
     * updates a user if there have been changes made to their profile
     * @param user user to update
     */
    void syncUser(User user);

    /**
     * creates a new message and adds it to list of messages of the Chat
     * calls receiveMessage to push the new message to both users
     * @param txt the message text
     * @param chatId the chat to which the message is to be adde
     * @return true if succeeded, false if something went wront
     */
    boolean sendMessage(String txt, String chatId);

    /**
     * creates a new post and adds it to the list of posts of the feed
     * @param txt text for the post
     * @return true if succeeded, false if something went wrong
     */
    boolean newPost(String txt, String email);

    /**
     * updates the given post with the given text
     * @param post post to update
     * @param text text to change current text to
     */
    void updatePost(Post post, String text);

    /**
     * deletes a post from its feeds
     * @param post
     */
    void deletePost(Post post);
}

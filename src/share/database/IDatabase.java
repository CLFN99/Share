package share.database;

import java.sql.SQLException;
import java.util.List;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import share.models.*;
public interface IDatabase {
    /**
     * this contains all the methods for the share.tests.database
     */

    /**
     * gets all the feeds
     * @return
     */
   // List<Feed> getFeeds();

//    /**
//     * gets a user's chats
//     * @param u
//     * @return
//     */
//    List<Chat> getChats(User u);
    /**
     * gets all chats
     * @return
     */
    List<Chat> getChats(List<User> users);
    /**
     * saves a message
     * @param msg message to save
     * @return true if succeeded, false if something went wrong
     */
    boolean saveMessage(Message msg);

    /**
     * saves a chat in the share.tests.database
     * @param c chat to save
     * @return true if succeeded, false if something went wrong
     */
    boolean saveChat(Chat c);
    /**
     * searches the share.tests.database for users with given username
     * @param username username to search for
     * @return list of users found, null if not found
     */
    List<User> searchUser(String username);

    /**
     * saves new user in database
     * @param u user to save
     * @return the auto generated ID
     */
    int saveUser(User u) throws MySQLIntegrityConstraintViolationException;

    /**
     * checks if the combination of email and password exist in the share.tests.database
     * @param email email to log in with
     * @param password user's password
     * @return a User if right email and password, null if not
     */
    User logIn(String email, String password);

    /**
     * updates a user's bio
     * @param u user to update
     * @return true if succeeded, false if not
     */
    boolean updateUser(User u);

    /**
     * saves a new post in the share.tests.database
     * @param p post to update
     * @return post id
     */
    int savePost(Post p);

    /**
     * updates a post's text
     * @param p post to update
     * @return true if succeeded, false if not
     */
    boolean updatePost(Post p);

    /**
     * deletes post from share.tests.database
     * @param p post to delete
     * @return true if succeeded, false if not
     */
    boolean deletePost(Post p);

    /**
     * saves a user's friend in the share.tests.database
     * @param u user
     * @param friend user's friend
     * @return true if succeeded, false if not
     */
    boolean addFriend(User u, User friend)throws MySQLIntegrityConstraintViolationException;

    /**
     * gets a user's feed
     * @param u user
     * @return retrieved feed
     */

//    Feed getFeed(User u);
//
//    /**
//     * gets a user's friends
//     * @param u user
//     * @return the user's friends list
//     */
//    List<User> getFriends(User u);

    /**
     * gets all users out of the database
     * @return list of users
     */
    List<User> getAllUsers();

    List<User> getFeedPosts(List<User> users);

    List<User> getFriends(List<User> users);
    void closeConn() throws SQLException;
}
